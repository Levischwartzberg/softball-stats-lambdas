package org.morts.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.morts.dto.TeamBattingByExitVelocityDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeamBattingByExitVelocityLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    String dbUrl = System.getenv("DB_URL");
    String dbUser = System.getenv("DB_USER");
    String dbPassword = System.getenv("DB_PASSWORD");

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {

        List<String> validLaunchAngles = List.of("ALL", "GROUNDBALL", "LINER", "FLYBALL", "POPUP");
        String launchAngle = event.getPathParameters().get("launchAngle");

        if (!validLaunchAngles.contains(launchAngle)) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody("Missing launchAngle parameter, use ALL, GROUNDBALL, LINER, FLYBALL, or POPUP");
        }

        List<TeamBattingByExitVelocityDTO> teamBattingByExitVelocityDTOList = new ArrayList<>();
        try {
            teamBattingByExitVelocityDTOList = getTeamBattingByVelocity(launchAngle);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(teamBattingByExitVelocityDTOList.toString());
    }

    public TeamBattingByExitVelocityLambda() {
    }

    public List<TeamBattingByExitVelocityDTO> getTeamBattingByVelocity(String launchAngle) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPassword);
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT exit_velocity, " +
                        "       COUNT(*) AS at_bats, " +
                        "       SUM(CASE WHEN hit = 1 THEN 1 ELSE 0 END) AS hits, " +
                        "       SUM(CASE WHEN single = 1 THEN 1 WHEN `double` = 1 THEN 2 WHEN triple = 1 THEN 3 WHEN homerun = 1 THEN 4 ELSE 0 END) AS total_bases " +
                        "FROM at_bats " +
                        "WHERE exit_velocity IS NOT NULL " +
                        "AND launch_angle LIKE ? " +
                        "GROUP BY exit_velocity " +
                        "ORDER BY exit_velocity ASC;"
        );
        preparedStatement.setString(1, launchAngle.equals("ALL") ? "%" : launchAngle);

        ResultSet rs = preparedStatement.executeQuery();

        List<TeamBattingByExitVelocityDTO> teamBattingByExitVelocityList = new ArrayList<>();
        while (rs.next()) {

            TeamBattingByExitVelocityDTO teamBattingByExitVelocityDTO = TeamBattingByExitVelocityDTO.builder()
                    .exitVelocity(rs.getInt("exit_velocity"))
                    .atBats(rs.getInt("at_bats"))
                    .hits(rs.getInt("hits"))
                    .totalBases(rs.getInt("total_bases"))
                    .build();

            teamBattingByExitVelocityList.add(teamBattingByExitVelocityDTO);
        }

        return teamBattingByExitVelocityList;
    }
}
