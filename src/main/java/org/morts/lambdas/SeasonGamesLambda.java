package org.morts.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.morts.domain.Opponent;
import org.morts.domain.Season;
import org.morts.dto.GameInfoDTO;
import org.morts.dto.SeasonGames;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SeasonGamesLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    String dbUrl = System.getenv("DB_URL");
    String dbUser = System.getenv("DB_USER");
    String dbPassword = System.getenv("DB_PASSWORD");

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        String seasonId = event.getPathParameters().get("seasonId");

        if (seasonId == null) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody("Missing seasonId parameter");
        }

        SeasonGames seasonGames;
        try {
            seasonGames = getSeasonResults(Integer.valueOf(seasonId));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(seasonGames.toString());
    }

    public SeasonGamesLambda() {
    }

    public SeasonGames getSeasonResults(Integer seasonId) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPassword);
        PreparedStatement preparedStatement = connection.prepareStatement("select game_info.*, s.session, s.year from game_info\n" +
                "left join seasons s on s.season_id = game_info.season_id\n" +
                "where s.season_id = ?;");
        preparedStatement.setInt(1, seasonId);
        ResultSet rs = preparedStatement.executeQuery();

        Season season = null;

        List<GameInfoDTO> games = new ArrayList<>();
        while (rs.next()) {
            season = Season.builder().session(rs.getString("session")).year(rs.getInt("year")).build();

            GameInfoDTO gameInfo = GameInfoDTO.builder()
                    .gameInfoId(rs.getInt("game_info_id"))
                    .home(determineHomeAway(rs))
                    .runsFor(rs.getInt("runs_for"))
                    .runsAgainst(rs.getInt("runs_against"))
                    .opponent(Opponent.builder()
                            .id(rs.getInt("opponent_id"))
                            .teamName("opponent_team_name")
                            .build())
                    .field(rs.getString("field"))
                    .temperature(rs.getInt("temperature"))
                    .weatherConditions(Optional.ofNullable(rs.getString("weather_conditions")).map(conditions -> conditions.split(",")).map(Arrays::asList).orElse(null))
                    .gameNotes(rs.getString("game_notes"))
                    .date(rs.getTimestamp("game_date_time"))
                    .build();

            games.add(gameInfo);
        }
        return SeasonGames.builder()
                .season(season)
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