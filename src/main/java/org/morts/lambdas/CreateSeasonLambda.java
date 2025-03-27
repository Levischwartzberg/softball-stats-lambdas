package org.morts.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.morts.domain.Season;
import org.morts.util.SqlFormatterUtil;

import java.sql.*;
import java.util.Map;

public class CreateSeasonLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    String dbUrl = System.getenv("DB_URL");
    String dbUser = System.getenv("DB_USER");
    String dbPassword = System.getenv("DB_PASSWORD");

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {

        Season season;

        try {
            season = objectMapper.readValue(event.getBody(), Season.class);
        } catch (Exception e) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody("Invalid request body: " + e.getMessage());
        }

        try {
            Season createdSeason = createSeason(season);

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(201)
                    .withBody(objectMapper.writeValueAsString(Map.of(
                            "message", "season created successfully",
                            "season", createdSeason
                    )))
                    .withHeaders(Map.of("Content-Type", "application/json"));
        } catch (Exception e){
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody("Error creating season: " + e.getMessage());
        }
    }

    public Season createSeason(Season season) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        try (Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPassword)) {
            String seasonValues = String.format("(%s, %s)",
                    SqlFormatterUtil.formatString(season.getSession()),
                    season.getYear());
            PreparedStatement preparedStatement = connection.prepareStatement("insert into season (session, year) \n" +
                    "values" + seasonValues, Statement.RETURN_GENERATED_KEYS);

            int updatedRows = preparedStatement.executeUpdate();

            if (updatedRows > 0) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    season.setId(id);
                }
            }
        }

        return season;
    }
}