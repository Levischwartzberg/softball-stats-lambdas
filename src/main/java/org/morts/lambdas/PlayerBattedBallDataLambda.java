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

public class PlayerBattedBallDataLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

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

        PlayerBattedBallDataDTO playerBattedBallDataDTO;
        try {
            playerBattedBallDataDTO = getPlayerBattedBallData(Integer.valueOf(playerId));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(playerBattedBallDataDTO.toString());
    }

    public PlayerBattedBallDataLambda() {
    }

    public PlayerBattedBallDataDTO getPlayerBattedBallData(Integer playerId) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPassword);
        PreparedStatement preparedStatement = connection.prepareStatement("select\n" +
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
                "where player_id = ?\n" +
                "  and (exit_velocity is not null\n" +
                "        or region is not null\n" +
                "        or launch_angle is not null);");
        preparedStatement.setInt(1, playerId);
        ResultSet rs = preparedStatement.executeQuery();

        List<BattedBallDataDTO> battedBallDataDTOList = new ArrayList<>();
        while (rs.next()) {

            Integer exit_velocity = rs.getInt("exit_velocity");
            String launch_angle = rs.getString("launch_angle");
            String region = rs.getString("region");
            String result = rs.getString("result");

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
                    .build();

            battedBallDataDTOList.add(battedBallDataDTO);
        }


        return PlayerBattedBallDataDTO.builder()
                .playerId(playerId)
                .battedBallData(battedBallDataDTOList)
                .build();
    }
}
