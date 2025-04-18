package org.morts.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.morts.domain.Player;

import java.sql.*;
public class PlayerInfoLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

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

        Player player;
        try {
            player = getPlayer(Integer.valueOf(playerId));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(player.toString());
    }

    public PlayerInfoLambda() {
    }

    public Player getPlayer(Integer playerId) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPassword);
        PreparedStatement preparedStatement = connection.prepareStatement("select * from players\n" +
                "where player_id = ?");
        preparedStatement.setInt(1, playerId);
        ResultSet rs = preparedStatement.executeQuery();

        Player player = null;
        while (rs.next()) {

            player = Player.builder()
                    .id(rs.getInt("player_id"))
                    .firstName(rs.getString("first_name"))
                    .lastName(rs.getString("last_name"))
                    .height(rs.getString("height"))
                    .weight(rs.getInt("weight"))
                    .throwHand(rs.getString("throw_hand"))
                    .batHand(rs.getString("bat_hand"))
                    .birthdate(rs.getDate("birthdate"))
                    .build();
        }

        return player;
    }
}