package org.morts.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.morts.dto.AtBatDTO;
import org.morts.dto.GameInfoDTO;
import org.morts.dto.InningDTO;
import org.morts.domain.Player;
import org.morts.dto.GameScorekeepingDTO;
import org.morts.enumeration.ResultENUM;
import org.morts.util.SqlFormatterUtil;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CreateScorekeepingGameLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    String dbUrl = System.getenv("DB_URL");
    String dbUser = System.getenv("DB_USER");
    String dbPassword = System.getenv("DB_PASSWORD");

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {

        GameScorekeepingDTO gameScorekeepingDTO;

        try {
            gameScorekeepingDTO = objectMapper.readValue(event.getBody(), GameScorekeepingDTO.class);
        } catch (Exception e) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody("Invalid request body: " + e.getMessage());
        }

        try {
            GameInfoDTO createdGameInfoDTO = createGameInfo(gameScorekeepingDTO.getGameInfo(), gameScorekeepingDTO.getSeason().getId());
            createInnings(gameScorekeepingDTO.getInnings(), createdGameInfoDTO.getGameInfoId());

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(201)
                    .withBody(objectMapper.writeValueAsString(Map.of(
                            "message", "player created successfully",
                            "createdGameInfo", createdGameInfoDTO
                    )))
                    .withHeaders(Map.of("Content-Type", "application/json"));
        } catch (Exception e) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody("Error creating player: " + e.getMessage());
        }
    }

    public GameInfoDTO createGameInfo(GameInfoDTO gameInfoDTO, Integer seasonId) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        try (Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPassword)) {
            String gameInfoValues = String.format("(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)",
                    seasonId,
                    gameInfoDTO.getHome(),
                    gameInfoDTO.getRunsFor(),
                    gameInfoDTO.getRunsAgainst(),
                    gameInfoDTO.getOpponent().getId(),
                    SqlFormatterUtil.formatString(new java.sql.Timestamp(gameInfoDTO.getDate().getTime()).toString()),
                    SqlFormatterUtil.formatString(gameInfoDTO.getField()),
                    gameInfoDTO.getTemperature(),
                    SqlFormatterUtil.formatString(String.join(",", gameInfoDTO.getWeatherConditions())),
                    SqlFormatterUtil.formatString(gameInfoDTO.getGameNotes()));
            PreparedStatement preparedStatement = connection.prepareStatement("insert into game_info (season_id, home_away, runs_for, runs_against, opponent_id, game_date_time, field, temperature, weather_conditions, game_notes) \n" +
                    "values" + gameInfoValues, Statement.RETURN_GENERATED_KEYS);

            int updatedRows = preparedStatement.executeUpdate();

            if (updatedRows > 0) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    gameInfoDTO.setGameInfoId(id);
                }
            }
        }

        return gameInfoDTO;
    }

    public List<InningDTO> createInnings(List<InningDTO> inningDTOS, Integer gameInfoId) throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        inningDTOS.forEach(inningDTO -> {

            try (Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPassword)) {
                String inningValues = String.format("(%s, %s, %s)",
                        inningDTO.getInning(),
                        inningDTO.getOpponentRuns(),
                        gameInfoId);
                PreparedStatement preparedStatement = connection.prepareStatement("insert into innings (inning, opponent_runs, game_info_id) \n" +
                        "values" + inningValues, Statement.RETURN_GENERATED_KEYS);

                int updatedRows = preparedStatement.executeUpdate();

                if (updatedRows > 0) {
                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int inningId = generatedKeys.getInt(1);

                        createAtBats(inningDTO.getAtBats(), inningId);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        return inningDTOS;
    }

    public List<AtBatDTO> createAtBats(List<AtBatDTO> atBatDTOS, Integer inningId) throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        atBatDTOS.forEach(atBatDTO -> {
            try (Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPassword)) {
                String atBatValues = String.format("(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)",
                        atBatDTO.getPlayer().getId(),
                        inningId,
                        atBatDTO.getInningIndex(),
                        Optional.ofNullable(atBatDTO.getBaserunners().getFirst()).map(Player::getId).orElse(null),
                        Optional.ofNullable(atBatDTO.getBaserunners().getSecond()).map(Player::getId).orElse(null),
                        Optional.ofNullable(atBatDTO.getBaserunners().getThird()).map(Player::getId).orElse(null),
                        atBatDTO.getResult().equals(ResultENUM.WALK) ? 0 : 1,
                        (atBatDTO.getResult().equals(ResultENUM.WALK) || atBatDTO.getResult().equals(ResultENUM.OUT) || atBatDTO.getResult().equals(ResultENUM.ERROR)) ? 0 : 1,
                        atBatDTO.getResult().equals(ResultENUM.SINGLE) ? 1 : 0,
                        atBatDTO.getResult().equals(ResultENUM.DOUBLE) ? 1 : 0,
                        atBatDTO.getResult().equals(ResultENUM.TRIPLE) ? 1 : 0,
                        atBatDTO.getResult().equals(ResultENUM.HOMERUN) ? 1 : 0,
                        atBatDTO.getResult().equals(ResultENUM.WALK) ? 1 : 0,
                        SqlFormatterUtil.formatString(atBatDTO.getScoring()),
                        atBatDTO.getRegion(),
                        SqlFormatterUtil.formatString(Optional.ofNullable(atBatDTO.getLaunchAngle()).map(String::valueOf).orElse(null)),
                        atBatDTO.getContactQuality(),
                        atBatDTO.getBallsAndStrikes()
                        );
                PreparedStatement preparedStatement = connection.prepareStatement("insert into at_bats (player_id, inning_id, inning_index, first_base, second_base, third_base, ab, hit, single, `double`, triple, homerun, walk, scoring, region, launch_angle, contact_quality, balls_and_strikes) \n" +
                        "values" + atBatValues, Statement.RETURN_GENERATED_KEYS);

                int updatedRows = preparedStatement.executeUpdate();

                if (updatedRows > 0) {
                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int atBatId = generatedKeys.getInt(1);

                        if (atBatDTO.getOuts().size() > 0) {
                            createAtBatOuts(atBatDTO.getOuts(), atBatId);
                        }
                        if (atBatDTO.getRuns().size() > 0) {
                            createAtBatRuns(atBatDTO.getRuns(), atBatId);
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        return atBatDTOS;
    }

    public void createAtBatOuts(List<Player> playersOut, Integer atBatId) throws ClassNotFoundException {

        playersOut.forEach(player -> {
            try (Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPassword)) {
                String atBatOutsValues = String.format("(%s, %s)",
                        atBatId,
                        player.getId()
                );
                PreparedStatement preparedStatement = connection.prepareStatement("insert into at_bat_outs (at_bat_id, player_id) \n" +
                        "values" + atBatOutsValues, Statement.RETURN_GENERATED_KEYS);

                preparedStatement.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void createAtBatRuns(List<Player> playersScored, Integer atBatId) throws ClassNotFoundException {

        playersScored.forEach(player -> {
            try (Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPassword)) {
                String atBatOutsValues = String.format("(%s, %s)",
                        atBatId,
                        player.getId()
                );
                PreparedStatement preparedStatement = connection.prepareStatement("insert into at_bat_runs (at_bat_id, player_id) \n" +
                        "values" + atBatOutsValues, Statement.RETURN_GENERATED_KEYS);

                preparedStatement.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
