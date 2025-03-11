package org.morts.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.morts.dto.PlayerStatline;
import org.morts.util.StatCalculatorUtil;

import java.sql.*;
import java.util.List;

public class SeasonTeamStatsLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    String dbUrl = System.getenv("DB_URL");
    String dbUser = System.getenv("DB_USER");
    String dbPassword = System.getenv("DB_PASSWORD");

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        String seasonId = event.getPathParameters().get("seasonId");

        if (seasonId == null) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody("Missing seasonId parameter");
        }

        List<PlayerStatline> playerStatlines;
        try {
            playerStatlines = getSeasonTeamStats(Integer.valueOf(seasonId));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(playerStatlines.toString());
    }

    public SeasonTeamStatsLambda() {
    }

    public List<PlayerStatline> getSeasonTeamStats(Integer seasonId) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPassword);
        PreparedStatement preparedStatement = connection.prepareStatement("select count(*) as games, p.id as player_id, p.last_name as last_name, p.first_name as first_name,\n" +
                        "       sum(g.at_Bats) as at_bats,\n" +
                        "       sum(g.hits) as hits,\n" +
                        "       sum(g.singles) as singles,\n" +
                        "       sum(g.doubles) as doubles,\n" +
                        "       sum(g.triples) as triples,\n" +
                        "       sum(g.homeruns) as homeruns,\n" +
                        "       sum(g.walks) as walks,\n" +
                        "       sum(g.runs) as runs,\n" +
                        "       sum(g.rbi) as rbi\n" +
                        "from game g\n" +
                        "left join player p on g.player_id = p.id\n" +
                        "left join result r on g.result_id = r.id\n" +
                        "left join season s on r.season_id = s.id\n" +
                        "where s.id = " + seasonId + " \n" +
                        "group by player_id\n" +
                        "order by at_bats desc;");
        ResultSet rs = preparedStatement.executeQuery();
        return StatCalculatorUtil.getSeasonTeamStats(rs);
    }
}
