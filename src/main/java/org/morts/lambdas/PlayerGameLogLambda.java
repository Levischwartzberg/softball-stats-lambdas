package org.morts.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.morts.domain.Result;
import org.morts.dto.ResultStatline;
import org.morts.dto.Statline;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerGameLogLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    String dbUrl = System.getenv("DB_URL");
    String dbUser = System.getenv("DB_USER");
    String dbPassword = System.getenv("DB_PASSWORD");

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        String playerId = event.getPathParameters().get("playerId");
        String seasonId = event.getPathParameters().get("seasonId");

        if (playerId == null) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody("Missing playerId parameter");
        }
        if (seasonId == null) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody("Missing seasonId parameter");
        }

        List<ResultStatline> resultStatlines;
        try {
            resultStatlines = getPlayerGameLog(Integer.valueOf(playerId), Integer.valueOf(seasonId));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(resultStatlines.toString());
    }

    public PlayerGameLogLambda() {
    }

    public List<ResultStatline> getPlayerGameLog(Integer playerId, Integer seasonId) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPassword);
        PreparedStatement preparedStatement = connection.prepareStatement("select r.date, r.id, g.* from game g\n" +
                "left join result r on r.id = g.result_id\n" +
                "left join season s on r.season_id = s.id\n" +
                "where g.player_id = " + playerId + " \n" +
                "and s.id = " + seasonId + ";");
        ResultSet rs = preparedStatement.executeQuery();

        List<ResultStatline> resultStatlines = new ArrayList<>();
        while (rs.next()) {

            ResultStatline resultStatline = ResultStatline.builder()
                    .result(
                            Result.builder()
                                    .id(rs.getInt("result_id"))
                                    .date(rs.getDate("date"))
                                    .build()
                    )
                    .statline(
                            Statline.builder()
                                    .atBats(rs.getInt("at_bats"))
                                    .lineupSpot(rs.getInt("lineup_spot"))
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

            resultStatlines.add(resultStatline);
        }

        return resultStatlines;
    }
}
