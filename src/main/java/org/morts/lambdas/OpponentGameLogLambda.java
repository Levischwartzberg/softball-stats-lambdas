package org.morts.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.morts.domain.Opponent;
import org.morts.domain.Season;
import org.morts.dto.GameInfoDTO;
import org.morts.dto.OpponentGames;
import org.morts.dto.SeasonGames;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class OpponentGameLogLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    String dbUrl = System.getenv("DB_URL");
    String dbUser = System.getenv("DB_USER");
    String dbPassword = System.getenv("DB_PASSWORD");

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        String opponentId = event.getPathParameters().get("opponentId");

        if (opponentId == null) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody("Missing opponentId parameter");
        }

        OpponentGames opponentGames;
        try {
            opponentGames = getOpponentResults(Integer.valueOf(opponentId));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(opponentGames.toString());
    }

    public OpponentGameLogLambda() {
    }

    public OpponentGames getOpponentResults(Integer opponentId) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPassword);
        PreparedStatement preparedStatement = connection.prepareStatement("select game_info.*, s.session, s.year, o.team_name as opponent_name from game_info\n" +
                "left join seasons s on s.season_id = game_info.season_id\n" +
                "left join opponents o on o.opponent_id = game_info.opponent_id\n" +
                "where game_info.opponent_id = ?;");
        preparedStatement.setInt(1, opponentId);
        ResultSet rs = preparedStatement.executeQuery();

        Opponent opponent = null;

        List<GameInfoDTO> games = new ArrayList<>();
        while (rs.next()) {
            opponent = Opponent.builder()
                    .id(rs.getInt("opponent_id"))
                    .teamName(rs.getString("opponent_name"))
                    .build();

            GameInfoDTO gameInfo = GameInfoDTO.builder()
                    .gameInfoId(rs.getInt("game_info_id"))
                    .home(determineHomeAway(rs))
                    .runsFor(rs.getInt("runs_for"))
                    .runsAgainst(rs.getInt("runs_against"))
                    .field(rs.getString("field"))
                    .date(rs.getTimestamp("game_date_time"))
                    .build();

            games.add(gameInfo);
        }
        return OpponentGames.builder()
                .opponent(opponent)
                .games(games)
                .build();
    }

    private Boolean determineHomeAway(ResultSet rs) throws SQLException {

        Boolean homeAway = rs.getBoolean("home_away");
        if (rs.wasNull()) {
            return null;
        } else {
            return homeAway;
        }
    }
}
