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
        PreparedStatement preparedStatement = connection.prepareStatement("select * from (\n" +
                "select at_bats_with_rbi.player_id as player_id, at_bats_with_rbi.first_name, at_bats_with_rbi.last_name, count(distinct at_bats_with_rbi.game_info_id) as games, sum(at_bats_with_rbi.ab) as at_bats, sum(at_bats_with_rbi.hit) as hits, sum(at_bats_with_rbi.single) as singles, sum(at_bats_with_rbi.`double`) as doubles, sum(at_bats_with_rbi.triple) as triples, sum(at_bats_with_rbi.homerun) as homeruns, sum(at_bats_with_rbi.walk) as walks, sum(at_bats_with_rbi.rbi) as rbi, runs\n" +
                "from (select p.first_name, p.last_name, ab, hit, single, `double`, triple, homerun, walk, at_bats.player_id, abr.rbi as rbi, i.game_info_id as game_info_id from at_bats\n" +
                "left join innings i on at_bats.inning_id = i.inning_id\n" +
                "left join players p on at_bats.player_id = p.player_id\n" +
                "left join (select count(*) as rbi, at_bat_id from at_bat_runs group by at_bat_id) abr on at_bats.at_bat_id = abr.at_bat_id) as at_bats_with_rbi\n" +
                "left join (select count(*) as runs, at_bat_runs.player_id from at_bat_runs\n" +
                "inner join at_bats a on at_bat_runs.at_bat_id = a.at_bat_id\n" +
                "group by at_bat_runs.player_id) abrs on at_bats_with_rbi.player_id = abrs.player_id\n" +
                "group by at_bats_with_rbi.player_id\n" +
                "order by at_bats desc\n" +
                ") as team_lifetime_stats\n" +
                "where team_lifetime_stats." + field + "> " + value + ";");
        ResultSet rs = preparedStatement.executeQuery();
        return StatCalculatorUtil.getSeasonTeamStats(rs);
    }
}