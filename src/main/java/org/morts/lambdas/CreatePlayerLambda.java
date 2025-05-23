package org.morts.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.morts.domain.Player;
import org.morts.util.SqlFormatterUtil;

import java.sql.*;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

public class CreatePlayerLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    String dbUrl = System.getenv("DB_URL");
    String dbUser = System.getenv("DB_USER");
    String dbPassword = System.getenv("DB_PASSWORD");

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {

        Player player;

        try {
            player = objectMapper.readValue(event.getBody(), Player.class);
        } catch (Exception e) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody("Invalid request body: " + e.getMessage());
        }

        try {
            Player createdPlayer = createPlayer(player);

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(201)
                    .withBody(objectMapper.writeValueAsString(Map.of(
                            "message", "player created successfully",
                            "player", createdPlayer
                    )))
                    .withHeaders(Map.of("Content-Type", "application/json"));
        } catch (Exception e){
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody("Error creating player: " + e.getMessage());
        }
    }

    public Player createPlayer(Player player) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        try (Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPassword)) {
            String playerValues = String.format("(%s, %s, %s, %s, %s, %s, %s)",
                    SqlFormatterUtil.formatString(player.getFirstName()),
                    SqlFormatterUtil.formatString(player.getLastName()),
                    SqlFormatterUtil.formatString(player.getHeight()),
                    player.getWeight(),
                    SqlFormatterUtil.formatString(player.getBatHand()),
                    SqlFormatterUtil.formatString(player.getThrowHand()),
                    SqlFormatterUtil.formatString(Optional.ofNullable(player.getBirthdate()).map(Date::toString).orElse(null)));
            PreparedStatement preparedStatement = connection.prepareStatement("insert into players (first_name, last_name, height, weight, bat_hand, throw_hand, birthdate) \n" +
                    "values" + playerValues, Statement.RETURN_GENERATED_KEYS);

            int updatedRows = preparedStatement.executeUpdate();

            if (updatedRows > 0) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    player.setId(id);
                }
            }
        }

        return player;
    }
}
