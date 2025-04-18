package org.morts.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.morts.util.StatCalculatorUtil;
import org.morts.dto.Statline;

import java.sql.*;

public class PlayerLifetimeStatsLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

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

        Statline statline;
        try {
            statline = getPlayerLifetimeStats(Integer.valueOf(playerId));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(statline.toString());
    }

    public PlayerLifetimeStatsLambda() {
    }

    public Statline getPlayerLifetimeStats(Integer playerId) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPassword);

        PreparedStatement preparedStatement = connection.prepareStatement("select at_bats_with_rbi.game_info_id as game_infoId, sum(at_bats_with_rbi.ab) as at_bats, sum(at_bats_with_rbi.hit) as hits, sum(at_bats_with_rbi.single) as singles, sum(at_bats_with_rbi.`double`) as doubles, sum(at_bats_with_rbi.triple) as triples, sum(at_bats_with_rbi.homerun) as homeruns, sum(at_bats_with_rbi.walk) as walks, sum(at_bats_with_rbi.rbi) as rbi, runs as runs\n" +
                "from (select ab, hit, single, `double`, triple, homerun, walk, rbi, player_id, i.game_info_id as game_info_id from at_bats\n" +
                "inner join innings i on at_bats.inning_id = i.inning_id\n" +
                "left join (select count(*) as rbi, at_bat_id from at_bat_runs group by at_bat_id) abr on at_bats.at_bat_id = abr.at_bat_id\n" +
                "where at_bats.player_id = ?) as at_bats_with_rbi\n" +
                "left join (select i2.game_info_id as i2_giid, count(*) as runs from at_bat_runs\n" +
                "inner join at_bats a on at_bat_runs.at_bat_id = a.at_bat_id\n" +
                "inner join innings i2 on a.inning_id = i2.inning_id\n" +
                "where at_bat_runs.player_id = ?\n" +
                "group by i2.game_info_id) as runs on at_bats_with_rbi.game_info_id = runs.i2_giid\n" +
                "group by game_info_id;");
        preparedStatement.setInt(1, playerId);
        preparedStatement.setInt(2, playerId);
        ResultSet rs = preparedStatement.executeQuery();
        return StatCalculatorUtil.computeStatlineFromGameIterator(rs);
    }
}
