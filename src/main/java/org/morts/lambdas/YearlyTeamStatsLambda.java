package org.morts.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.morts.dto.PlayerStatline;
import org.morts.util.StatCalculatorUtil;

import java.sql.*;
import java.util.List;

public class YearlyTeamStatsLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    String dbUrl = System.getenv("DB_URL");
    String dbUser = System.getenv("DB_USER");
    String dbPassword = System.getenv("DB_PASSWORD");

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        String year = event.getPathParameters().get("year");

        if (year == null) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody("Missing year parameter");
        }

        List<PlayerStatline> playerStatlines;
        try {
            playerStatlines = getYearlyTeamStats(Integer.valueOf(year));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(playerStatlines.toString());
    }

    public YearlyTeamStatsLambda() {
    }

    public List<PlayerStatline> getYearlyTeamStats(Integer year) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPassword);
        PreparedStatement preparedStatement = connection.prepareStatement("select at_bats_with_rbi.player_id as player_id, at_bats_with_rbi.first_name, at_bats_with_rbi.last_name, count(distinct at_bats_with_rbi.game_info_id) as games, sum(at_bats_with_rbi.ab) as at_bats, sum(at_bats_with_rbi.hit) as hits, sum(at_bats_with_rbi.single) as singles, sum(at_bats_with_rbi.`double`) as doubles, sum(at_bats_with_rbi.triple) as triples, sum(at_bats_with_rbi.homerun) as homeruns, sum(at_bats_with_rbi.walk) as walks, sum(at_bats_with_rbi.rbi) as rbi, runs\n" +
                "    from (select p.first_name, p.last_name, ab, hit, single, `double`, triple, homerun, walk, at_bats.player_id, abr.rbi as rbi, gi.game_info_id as game_info_id from at_bats\n" +
                "    left join innings i on at_bats.inning_id = i.inning_id\n" +
                "    left join game_info gi on i.game_info_id = gi.game_info_id\n" +
                "    left join seasons s on gi.season_id = s.season_id\n" +
                "    left join players p on at_bats.player_id = p.player_id\n" +
                "    left join (select count(*) as rbi, at_bat_id from at_bat_runs group by at_bat_id) abr on at_bats.at_bat_id = abr.at_bat_id\n" +
                "    where s.year = ?) as at_bats_with_rbi\n" +
                "    left join (select count(*) as runs, at_bat_runs.player_id from at_bat_runs\n" +
                "    inner join at_bats a on at_bat_runs.at_bat_id = a.at_bat_id\n" +
                "    inner join innings i2 on a.inning_id = i2.inning_id\n" +
                "    inner join game_info g on i2.game_info_id = g.game_info_id\n" +
                "    inner join seasons s2 on g.season_id = s2.season_id\n" +
                "    where s2.year = ?\n" +
                "    group by at_bat_runs.player_id) abrs on at_bats_with_rbi.player_id = abrs.player_id\n" +
                "    group by at_bats_with_rbi.first_name, at_bats_with_rbi.last_name\n" +
                "    order by at_bats desc;");
        preparedStatement.setInt(1, year);
        preparedStatement.setInt(2, year);
        ResultSet rs = preparedStatement.executeQuery();
        return StatCalculatorUtil.getSeasonTeamStats(rs);
    }
}
