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
        PreparedStatement preparedStatement = connection.prepareStatement("select at_bats_with_rbi.season_id as season_id, at_bats_with_rbi.year as year, at_bats_with_rbi.session as session, count(distinct at_bats_with_rbi.game_info_id) as games, sum(at_bats_with_rbi.ab) as at_bats, sum(at_bats_with_rbi.hit) as hits, sum(at_bats_with_rbi.single) as singles, sum(at_bats_with_rbi.`double`) as doubles, sum(at_bats_with_rbi.triple) as triples, sum(at_bats_with_rbi.homerun) as homeruns, sum(at_bats_with_rbi.walk) as walks, sum(at_bats_with_rbi.rbi) as rbi, runs as runs\n" +
                "from (select ab, hit, single, `double`, triple, homerun, walk, rbi, player_id, gi.game_info_id as game_info_id, gi.season_id as season_id, s.year as year, s.session as session from at_bats\n" +
                "inner join innings i on at_bats.inning_id = i.inning_id\n" +
                "inner join game_info gi on i.game_info_id = gi.game_info_id\n" +
                "inner join seasons s on gi.season_id = s.season_id\n" +
                "left join (select count(*) as rbi, at_bat_id from at_bat_runs group by at_bat_id) abr on at_bats.at_bat_id = abr.at_bat_id\n" +
                "where at_bats.player_id = ?) as at_bats_with_rbi\n" +
                "left join (select g.season_id as g_season_id, count(*) as runs from at_bat_runs\n" +
                "inner join at_bats a on at_bat_runs.at_bat_id = a.at_bat_id\n" +
                "inner join innings i2 on a.inning_id = i2.inning_id\n" +
                "inner join game_info g on i2.game_info_id = g.game_info_id\n" +
                "where at_bat_runs.player_id = ?\n" +
                "group by g.season_id) as runs on at_bats_with_rbi.season_id = runs.g_season_id\n" +
                "group by season_id;");
        preparedStatement.setInt(1, playerId);
        preparedStatement.setInt(2, playerId);
        ResultSet rs = preparedStatement.executeQuery();
        return StatCalculatorUtil.getSeasonStats(rs);
    }
}
