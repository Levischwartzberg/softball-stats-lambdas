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
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT\n" +
                "    at_bats_with_rbi.player_id AS player_id,\n" +
                "    at_bats_with_rbi.first_name,\n" +
                "    at_bats_with_rbi.last_name,\n" +
                "    COUNT(DISTINCT at_bats_with_rbi.game_info_id) AS games,\n" +
                "    SUM(at_bats_with_rbi.ab) AS at_bats,\n" +
                "    SUM(at_bats_with_rbi.hit) AS hits,\n" +
                "    SUM(at_bats_with_rbi.single) AS singles,\n" +
                "    SUM(at_bats_with_rbi.`double`) AS doubles,\n" +
                "    SUM(at_bats_with_rbi.triple) AS triples,\n" +
                "    SUM(at_bats_with_rbi.homerun) AS homeruns,\n" +
                "    SUM(at_bats_with_rbi.walk) AS walks,\n" +
                "    SUM(at_bats_with_rbi.rbi) AS rbi,\n" +
                "    runs,\n" +
                "    ROUND(\n" +
                "        (\n" +
                "            (SUM(at_bats_with_rbi.ab) - SUM(at_bats_with_rbi.hit)) * ov_out.runs_above_average +\n" +
                "            SUM(at_bats_with_rbi.walk) * ov_walk.runs_above_average +\n" +
                "            SUM(at_bats_with_rbi.single) * ov_single.runs_above_average +\n" +
                "            SUM(at_bats_with_rbi.`double`) * ov_double.runs_above_average +\n" +
                "            SUM(at_bats_with_rbi.triple) * ov_triple.runs_above_average +\n" +
                "            SUM(at_bats_with_rbi.homerun) * ov_hr.runs_above_average\n" +
                "            ) * 100 / (SUM(at_bats_with_rbi.ab) + SUM(at_bats_with_rbi.walk)) + 100\n" +
                "        ) AS wrc_plus\n" +
                "FROM (\n" +
                "         SELECT\n" +
                "             p.first_name,\n" +
                "             p.last_name,\n" +
                "             ab,\n" +
                "             hit,\n" +
                "             single,\n" +
                "             `double`,\n" +
                "             triple,\n" +
                "             homerun,\n" +
                "             walk,\n" +
                "             at_bats.player_id,\n" +
                "             abr.rbi AS rbi,\n" +
                "             gi.game_info_id AS game_info_id\n" +
                "         FROM at_bats\n" +
                "                  LEFT JOIN innings i ON at_bats.inning_id = i.inning_id\n" +
                "                  LEFT JOIN game_info gi ON i.game_info_id = gi.game_info_id\n" +
                "                  LEFT JOIN seasons s ON gi.season_id = s.season_id\n" +
                "                  LEFT JOIN players p ON at_bats.player_id = p.player_id\n" +
                "                  LEFT JOIN (\n" +
                "             SELECT COUNT(*) AS rbi, at_bat_id\n" +
                "             FROM at_bat_runs\n" +
                "             GROUP BY at_bat_id\n" +
                "         ) abr ON at_bats.at_bat_id = abr.at_bat_id\n" +
                "         WHERE s.year = ?\n" +
                "     ) AS at_bats_with_rbi\n" +
                "         LEFT JOIN (\n" +
                "    SELECT COUNT(*) AS runs, at_bat_runs.player_id\n" +
                "    FROM at_bat_runs\n" +
                "             INNER JOIN at_bats a ON at_bat_runs.at_bat_id = a.at_bat_id\n" +
                "             INNER JOIN innings i2 ON a.inning_id = i2.inning_id\n" +
                "             INNER JOIN game_info g ON i2.game_info_id = g.game_info_id\n" +
                "             INNER JOIN seasons s2 ON g.season_id = s2.season_id\n" +
                "    WHERE s2.year = ?\n" +
                "    GROUP BY at_bat_runs.player_id\n" +
                ") abrs ON at_bats_with_rbi.player_id = abrs.player_id\n" +
                "         JOIN outcome_run_values ov_out ON ov_out.result = 'OUT'\n" +
                "         JOIN outcome_run_values ov_walk ON ov_walk.result = 'WALK'\n" +
                "         JOIN outcome_run_values ov_single ON ov_single.result = 'SINGLE'\n" +
                "         JOIN outcome_run_values ov_double ON ov_double.result = 'DOUBLE'\n" +
                "         JOIN outcome_run_values ov_triple ON ov_triple.result = 'TRIPLE'\n" +
                "         JOIN outcome_run_values ov_hr ON ov_hr.result = 'HOMERUN'\n" +
                "GROUP BY at_bats_with_rbi.player_id\n" +
                "ORDER BY at_bats DESC;");
        preparedStatement.setInt(1, year);
        preparedStatement.setInt(2, year);
        ResultSet rs = preparedStatement.executeQuery();
        return StatCalculatorUtil.getSeasonTeamStats(rs);
    }
}
