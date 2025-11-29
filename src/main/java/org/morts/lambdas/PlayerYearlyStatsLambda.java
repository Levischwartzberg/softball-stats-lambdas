package org.morts.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.morts.dto.SeasonStatline;
import org.morts.dto.YearlyStatline;
import org.morts.util.StatCalculatorUtil;

import java.sql.*;
import java.util.List;

public class PlayerYearlyStatsLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

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

        List<YearlyStatline> yearlyStatlines;
        try {
            yearlyStatlines = getPlayerYearlyStats(Integer.valueOf(playerId));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(yearlyStatlines.toString());
    }

    public PlayerYearlyStatsLambda() {
    }

    public List<YearlyStatline> getPlayerYearlyStats(Integer playerId) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPassword);
        PreparedStatement preparedStatement = connection.prepareStatement("select\n" +
                "    at_bats_with_rbi.year as year,\n" +
                "    count(distinct at_bats_with_rbi.game_info_id) as games,\n" +
                "    sum(at_bats_with_rbi.ab) as at_bats,\n" +
                "    sum(at_bats_with_rbi.hit) as hits,\n" +
                "    sum(at_bats_with_rbi.single) as singles,\n" +
                "    sum(at_bats_with_rbi.`double`) as doubles,\n" +
                "    sum(at_bats_with_rbi.triple) as triples,\n" +
                "    sum(at_bats_with_rbi.homerun) as homeruns,\n" +
                "    sum(at_bats_with_rbi.walk) as walks,\n" +
                "    sum(at_bats_with_rbi.rbi) as rbi,\n" +
                "    runs as runs,\n" +
                "    round(\n" +
                "            (\n" +
                "                (sum(at_bats_with_rbi.ab) - sum(at_bats_with_rbi.hit)) * ov_out.runs_above_average +\n" +
                "                sum(at_bats_with_rbi.walk) * ov_walk.runs_above_average +\n" +
                "                sum(at_bats_with_rbi.single) * ov_single.runs_above_average +\n" +
                "                sum(at_bats_with_rbi.`double`) * ov_double.runs_above_average +\n" +
                "                sum(at_bats_with_rbi.triple) * ov_triple.runs_above_average +\n" +
                "                sum(at_bats_with_rbi.homerun) * ov_hr.runs_above_average\n" +
                "                ) * 100 / (sum(at_bats_with_rbi.ab) + sum(at_bats_with_rbi.walk)) + 100\n" +
                "    ) as wrc_plus\n" +
                "from (\n" +
                "         select\n" +
                "             ab,\n" +
                "             hit,\n" +
                "             single,\n" +
                "             `double`,\n" +
                "             triple,\n" +
                "             homerun,\n" +
                "             walk,\n" +
                "             rbi,\n" +
                "             player_id,\n" +
                "             gi.game_info_id as game_info_id,\n" +
                "             s.year as year\n" +
                "         from at_bats\n" +
                "                  inner join innings i on at_bats.inning_id = i.inning_id\n" +
                "                  inner join game_info gi on i.game_info_id = gi.game_info_id\n" +
                "                  inner join seasons s on gi.season_id = s.season_id\n" +
                "                  left join outcome_run_values ov_out on ov_out.result = 'OUT'\n" +
                "                  left join (\n" +
                "             select count(*) as rbi, at_bat_id\n" +
                "             from at_bat_runs\n" +
                "             group by at_bat_id\n" +
                "         ) abr on at_bats.at_bat_id = abr.at_bat_id\n" +
                "         where at_bats.player_id = ?\n" +
                "     ) as at_bats_with_rbi\n" +
                "         left join (\n" +
                "    select\n" +
                "        s2.year as s2_year,\n" +
                "        count(*) as runs\n" +
                "    from at_bat_runs\n" +
                "             inner join at_bats a on at_bat_runs.at_bat_id = a.at_bat_id\n" +
                "             inner join innings i2 on a.inning_id = i2.inning_id\n" +
                "             inner join game_info g on i2.game_info_id = g.game_info_id\n" +
                "             inner join seasons s2 on g.season_id = s2.season_id\n" +
                "    where at_bat_runs.player_id = ?\n" +
                "    group by s2.year\n" +
                ") as runs on at_bats_with_rbi.year = runs.s2_year\n" +
                "    join outcome_run_values ov_out on ov_out.result = 'OUT'\n" +
                "    join outcome_run_values ov_walk on ov_walk.result = 'WALK'\n" +
                "    join outcome_run_values ov_single on ov_single.result = 'SINGLE'\n" +
                "    join outcome_run_values ov_double on ov_double.result = 'DOUBLE'\n" +
                "    join outcome_run_values ov_triple on ov_triple.result = 'TRIPLE'\n" +
                "    join outcome_run_values ov_hr on ov_hr.result = 'HOMERUN'\n" +
                "group by year;");
        preparedStatement.setInt(1, playerId);
        preparedStatement.setInt(2, playerId);
        ResultSet rs = preparedStatement.executeQuery();
        return StatCalculatorUtil.getYearlyStats(rs);
    }
}
