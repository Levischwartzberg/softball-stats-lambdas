package org.morts.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.morts.dto.PlayerStatline;
import org.morts.util.StatCalculatorUtil;

import java.sql.*;
import java.util.List;

public class TeamLifetimeStatsLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    String dbUrl = System.getenv("DB_URL");
    String dbUser = System.getenv("DB_USER");
    String dbPassword = System.getenv("DB_PASSWORD");

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        String field = event.getPathParameters().get("field");
        String value = event.getPathParameters().get("value");

        if (field == null) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody("Missing field parameter");
        }
        if (value == null) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody("Missing value parameter");
        }

        List<PlayerStatline> playerStatlines;
        try {
            playerStatlines = getTeamLifetimeStats(field, Integer.valueOf(value));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(playerStatlines.toString());
    }

    public TeamLifetimeStatsLambda() {
    }

    public List<PlayerStatline> getTeamLifetimeStats(String field, Integer value) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPassword);
        PreparedStatement preparedStatement = connection.prepareStatement("select * from (select count(*) as games, p.last_name, p.first_name, p.id as player_id,\n" +
                "       sum(g.at_bats) as at_bats, sum(g.hits) as hits, sum(g.singles) as singles,\n" +
                "       sum(g.doubles) as doubles, sum(g.triples) as triples, sum(g.homeruns) as homeruns,\n" +
                "       sum(g.walks) as walks, sum(g.rbi) as rbi, sum(g.runs) as runs from game g\n" +
                "left join player p on p.id = g.player_id\n" +
                "group by p.id\n" +
                "order by games desc) as team_stats where team_stats." +field + "  >"  + value + ";");
        ResultSet rs = preparedStatement.executeQuery();
        return StatCalculatorUtil.getSeasonTeamStats(rs);
    }
}