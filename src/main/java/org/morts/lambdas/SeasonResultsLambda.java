package org.morts.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.morts.domain.Result;
import org.morts.domain.Season;
import org.morts.dto.SeasonResults;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeasonResultsLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

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

        SeasonResults seasonResults;
        try {
            seasonResults = getSeasonResults(Integer.valueOf(seasonId));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(seasonResults.toString());
    }

    public SeasonResultsLambda() {
    }

    public SeasonResults getSeasonResults(Integer seasonId) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPassword);
        PreparedStatement preparedStatement = connection.prepareStatement("select s.year as year, s.session as session,\n" +
                "       r.id as result_id, r.result as result, r.score as score, r.date as date from result r\n" +
                "left join season s on r.season_id = s.id\n" +
                "where s.id = " + seasonId + " \n" +
                "order by r.date asc;");
        ResultSet rs = preparedStatement.executeQuery();

        Season season = null;

        List<Result> results = new ArrayList<>();
        while (rs.next()) {
            season = Season.builder().session(rs.getString("session")).year(rs.getInt("year")).build();

            Result result = Result.builder()
                    .id(rs.getInt("result_id"))
                    .result(rs.getString("result"))
                    .score(rs.getString("score"))
                    .date(rs.getDate("date"))
                    .build();

            results.add(result);
        }
        return SeasonResults.builder()
                .season(season)
                .results(results)
                .build();
    }
}