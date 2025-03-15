package org.morts.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.morts.domain.Opponent;
import org.morts.util.SqlFormatterUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class CreateOpponentLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    String dbUrl = System.getenv("DB_URL");
    String dbUser = System.getenv("DB_USER");
    String dbPassword = System.getenv("DB_PASSWORD");

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {

        Opponent opponent;

        try {
            opponent = objectMapper.readValue(event.getBody(), Opponent.class);
        } catch (Exception e) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody("Invalid request body: " + e.getMessage());
        }

        try {
            Opponent createdOpponent = createOpponent(opponent);

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(201)
                    .withBody(objectMapper.writeValueAsString(Map.of(
                            "message", "opponent created successfully",
                            "opponent", createdOpponent
                    )))
                    .withHeaders(Map.of("Content-Type", "application/json"));
        } catch (Exception e){
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody("Error creating opponent: " + e.getMessage());
        }
    }

    public Opponent createOpponent(Opponent opponent) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        try (Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPassword)) {
            String teamValues = String.format("(%s)",
                    SqlFormatterUtil.formatString(opponent.getTeamName()));
            PreparedStatement preparedStatement = connection.prepareStatement("insert into opponent (team_name) \n" +
                    "values" + teamValues);
            preparedStatement.executeUpdate();
        }

        return opponent;
    }
}
