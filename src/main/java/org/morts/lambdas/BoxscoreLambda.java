package org.morts.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.morts.domain.Player;
import org.morts.domain.Result;
import org.morts.dto.Boxscore;
import org.morts.dto.PlayerStatline;
import org.morts.dto.Statline;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BoxscoreLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    String dbUrl = System.getenv("DB_URL");
    String dbUser = System.getenv("DB_USER");
    String dbPassword = System.getenv("DB_PASSWORD");

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        String resultId = event.getPathParameters().get("resultId");

        if (resultId == null) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody("Missing resultId parameter");
        }

        Boxscore boxscore;
        try {
            boxscore = getBoxscore(Integer.valueOf(resultId));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(boxscore.toString());
    }

    public BoxscoreLambda() {
    }

    public Boxscore getBoxscore(Integer resultId) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPassword);
        PreparedStatement preparedStatement = connection.prepareStatement("select p.id as player_id, p.first_name as first_name, p.last_name as last_name,\n" +
                "       r.date as date, r.score as score, r.result as result,\n" +
                "       g.* from game g\n" +
                "left join player p on g.player_id = p.id\n" +
                "left join result r on g.result_id = r.id\n" +
                "where g.result_id = " + resultId + " \n" +
                "order by g.lineup_spot asc;");
        ResultSet rs = preparedStatement.executeQuery();

        Result result = null;

        List<PlayerStatline> playerStatlines = new ArrayList<>();
        while (rs.next()) {
             result = Result.builder()
                    .id(rs.getInt("result_id"))
                    .result(rs.getString("result"))
                    .score(rs.getString("score"))
                    .date(rs.getDate("date"))
                    .build();

            PlayerStatline playerStatline = PlayerStatline.builder()
                    .player(
                            Player.builder()
                                    .id(rs.getInt("player_id"))
                                    .firstName(rs.getString("first_name"))
                                    .lastName(rs.getString("last_name"))
                                    .build()
                    )
                    .statline(
                            Statline.builder()
                                    .atBats(rs.getInt("at_bats"))
                                    .hits(rs.getInt("hits"))
                                    .singles(rs.getInt("singles"))
                                    .doubles(rs.getInt("doubles"))
                                    .triples(rs.getInt("triples"))
                                    .homeruns(rs.getInt("homeruns"))
                                    .walks(rs.getInt("walks"))
                                    .runs(rs.getInt("runs"))
                                    .rbi(rs.getInt("rbi"))
                                    .avg(rs.getDouble("avg"))
                                    .obp(rs.getDouble("obp"))
                                    .slg(rs.getDouble("slg"))
                                    .ops(rs.getDouble("ops"))
                                    .build()
                    )
                    .build();

            playerStatlines.add(playerStatline);
        }
        return Boxscore.builder()
                .result(result)
                .playerStatlines(playerStatlines)
                .build();
    }
}
