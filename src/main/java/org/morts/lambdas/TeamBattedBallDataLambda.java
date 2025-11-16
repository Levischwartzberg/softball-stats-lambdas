package org.morts.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.morts.dto.BattedBallDataDTO;
import org.morts.dto.PlayerBattedBallDataDTO;
import org.morts.enumeration.LaunchAngleENUM;
import org.morts.enumeration.RegionENUM;
import org.morts.enumeration.ResultENUM;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeamBattedBallDataLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    String dbUrl = System.getenv("DB_URL");
    String dbUser = System.getenv("DB_USER");
    String dbPassword = System.getenv("DB_PASSWORD");

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {

        PlayerBattedBallDataDTO playerBattedBallDataDTO;
        try {
            playerBattedBallDataDTO = getTeamBattedBallData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(playerBattedBallDataDTO.toString());
    }

    public TeamBattedBallDataLambda() {
    }

    public PlayerBattedBallDataDTO getTeamBattedBallData() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPassword);
        PreparedStatement preparedStatement = connection.prepareStatement("select bbd.result, bbd.exit_velocity, bbd.region, bbd.launch_angle, orv.runs_above_average from (select\n" +
                "    exit_velocity,\n" +
                "    launch_angle,\n" +
                "    region,\n" +
                "    case\n" +
                "        when single = 1 then 'SINGLE'\n" +
                "        when `double` = 1 then 'DOUBLE'\n" +
                "        when triple = 1 then'TRIPLE'\n" +
                "        when homerun = 1 then 'HOMERUN'\n" +
                "        when hit = 0 then 'OUT'\n" +
                "        else 'Unknown'\n" +
                "        end as result\n" +
                "from at_bats\n" +
                "  where (exit_velocity is not null\n" +
                "        or region is not null\n" +
                "        or launch_angle is not null)) as bbd\n" +
                "left join outcome_run_values orv on bbd.result = orv.result;");
        ResultSet rs = preparedStatement.executeQuery();

        List<BattedBallDataDTO> battedBallDataDTOList = new ArrayList<>();
        while (rs.next()) {

            BattedBallDataDTO battedBallDataDTO = BattedBallDataDTO.builder()
                    .exitVelocity(rs.getInt("exit_velocity"))
                    .launchAngle(rs.getString("launch_angle") != null
                            ? LaunchAngleENUM.valueOf(rs.getString("launch_angle"))
                            : null)
                    .region(rs.getString("region") != null
                            ? RegionENUM.valueOf(rs.getString("region"))
                            : null)
                    .result(rs.getString("result") != null
                            ? ResultENUM.valueOf(rs.getString("result"))
                            : null)
                    .runsAboveAverage(rs.getDouble("runs_above_average"))
                    .build();

            battedBallDataDTOList.add(battedBallDataDTO);
        }


        return PlayerBattedBallDataDTO.builder()
                .playerId(null)
                .battedBallData(battedBallDataDTOList)
                .build();
    }
}
