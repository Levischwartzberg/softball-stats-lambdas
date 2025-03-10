package org.morts.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.morts.dto.SeasonStatline;
import org.morts.util.StatCalculatorUtil;

import java.sql.*;
import java.util.List;

public class PlayerSeasonStatsLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    String dbUrl = System.getenv("DB_URL");
    String dbUser = System.getenv("DB_USER");
    String dbPassword = System.getenv("DB_PASSWORD");

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        String playerId = event.getPathParameters().get("playerId");

        if (playerId == null) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody("Missing playerId parameter");
        }

        List<SeasonStatline> seasonStatlines;
        try {
            seasonStatlines = getPlayerSeasonStats(Integer.valueOf(playerId));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(seasonStatlines.toString());
    }

    public PlayerSeasonStatsLambda() {
    }

    public List<SeasonStatline> getPlayerSeasonStats(Integer playerId) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPassword);
        PreparedStatement preparedStatement = connection.prepareStatement("select s.*, count(*) as games, sum(g.at_bats) as at_bats,\n" +
                "       sum(g.hits) as hits,\n" +
                "       sum(g.singles) as singles,\n" +
                "       sum(g.doubles) as doubles,\n" +
                "       sum(g.triples) as triples,\n" +
                "       sum(g.homeruns) as homeruns,\n" +
                "       sum(g.walks) as walks,\n" +
                "       sum(g.rbi) as rbi,\n" +
                "       sum(g.runs) as runs from game g\n" +
                "left join result r on g.result_id = r.id\n" +
                "left join season s on s.id = r.season_id\n" +
                "where g.player_id = " + playerId + " \n" +
                "group by s.id\n" +
                "order by s.id asc;");
        ResultSet rs = preparedStatement.executeQuery();
        return StatCalculatorUtil.getSeasonStats(rs);
    }
}
