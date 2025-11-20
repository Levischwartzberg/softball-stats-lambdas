package org.morts.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.morts.dto.ResultRunExpectancyDTO;
import org.morts.dto.RunExpectancyDTO;
import org.morts.dto.SituationRunExpectancyDTO;
import org.morts.enumeration.ResultENUM;

import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RunExpectancyLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    String dbUrl = System.getenv("DB_URL");
    String dbUser = System.getenv("DB_USER");
    String dbPassword = System.getenv("DB_PASSWORD");

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {

        RunExpectancyDTO runExpectancyDTO;
        try {
            runExpectancyDTO = getRunExpectancy();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(runExpectancyDTO.toString());
    }

    public RunExpectancyLambda() {}

    public RunExpectancyDTO getRunExpectancy() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPassword);
        PreparedStatement preparedStatement = connection.prepareStatement("select * from run_expectancy_matrix;");
        ResultSet situationRS = preparedStatement.executeQuery();

        Set<SituationRunExpectancyDTO> situationRunExpectancy = new HashSet<>();
        while (situationRS.next()) {
            SituationRunExpectancyDTO situationRunExpectancyDTO = SituationRunExpectancyDTO.builder()
                    .firstBaseOccupied(situationRS.getBoolean("first_base"))
                    .secondBaseOccupied(situationRS.getBoolean("second_base"))
                    .thirdBaseOccupied(situationRS.getBoolean("third_base"))
                    .outs(situationRS.getInt("outs"))
                    .runExpectancy(situationRS.getDouble("avg_runs_after"))
                    .build();

            situationRunExpectancy.add(situationRunExpectancyDTO);
        }

        PreparedStatement preparedStatement2 = connection.prepareStatement("select * from outcome_run_values;");
        ResultSet outcomeRS = preparedStatement2.executeQuery();

        Map<ResultENUM, Double> runExpectancyByResult = new HashMap<>();
        while (outcomeRS.next()) {
            ResultENUM resultENUM = ResultENUM.valueOf(outcomeRS.getString("result"));
            Double runsAboveAverage = outcomeRS.getDouble("runs_above_average");

            runExpectancyByResult.put(resultENUM, runsAboveAverage);
        }

        ResultRunExpectancyDTO resultRunExpectancyDTO = ResultRunExpectancyDTO.builder()
                .runExpectancyByResult(runExpectancyByResult)
                .build();

        return RunExpectancyDTO.builder()
                .resultRunExpectancy(resultRunExpectancyDTO)
                .situationRunExpectancy(situationRunExpectancy)
                .build();
    }

}
