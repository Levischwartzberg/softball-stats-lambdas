package org.morts.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.morts.domain.Player;
import org.morts.dto.*;
import org.morts.enumeration.LaunchAngleENUM;
import org.morts.enumeration.RegionENUM;
import org.morts.enumeration.ResultENUM;

import java.sql.*;
import java.util.*;

public class ScoresheetLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    String dbUrl = System.getenv("DB_URL");
    String dbUser = System.getenv("DB_USER");
    String dbPassword = System.getenv("DB_PASSWORD");

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        String resultId = event.getPathParameters().get("gameInfoId");

        if (resultId == null) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody("Missing gameInfoId parameter");
        }

        Scoresheet scoresheet;
        try {
            scoresheet = getScoresheet(Integer.valueOf(resultId));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(scoresheet.toString());
    }

    public ScoresheetLambda() {
    }

    public Scoresheet getScoresheet(Integer gameInfoId) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPassword);
        PreparedStatement preparedStatement = connection.prepareStatement("select ab.*, p_out.first_name, p_out.last_name, p_scored.first_name, p_scored.last_name, p1b.first_name, p1b.last_name, p2b.first_name, p2b.last_name, p3b.first_name, p3b.last_name from (select i.inning, p.first_name, p.last_name, at_bats.*, abo.player_id as player_out, abr.player_id as player_scored from at_bats\n" +
                "left join at_bat_runs abr on at_bats.at_bat_id = abr.at_bat_id\n" +
                "left join at_bat_outs abo on at_bats.at_bat_id = abo.at_bat_id\n" +
                "left join players p on at_bats.player_id = p.player_id\n" +
                "left join innings i on at_bats.inning_id = i.inning_id\n" +
                "where i.game_info_id = ?) ab\n" +
                "left join players p1b on p1b.player_id = ab.first_base\n" +
                "left join players p2b on p2b.player_id = ab.second_base\n" +
                "left join players p3b on p3b.player_id = ab.third_base\n" +
                "left join players p_out on p_out.player_id = ab.player_out\n" +
                "left join players p_scored on p_scored.player_id = ab.player_scored\n" +
                "order by inning, ab.inning_index;");
        preparedStatement.setInt(1, gameInfoId);
        ResultSet rs = preparedStatement.executeQuery();

        Map<Integer, InningDTO> inningMap = new HashMap<>();
        Set<Integer> atBatIds = new HashSet<>();
        while (rs.next()) {

            Integer inning = rs.getInt("inning");

            if (!inningMap.containsKey(inning)) {
                InningDTO inningDTO = InningDTO.builder()
                        .inning(inning)
                        .atBats(new ArrayList<>())
                        .build();

                AtBatDTO atBatDTO = processAtBatDTO(rs);

                inningDTO.getAtBats().add(atBatDTO);

                inningMap.put(inning, inningDTO);
                atBatIds.add(atBatDTO.getAtBatId());
            } else {

                InningDTO inningDTO = inningMap.get(inning);

                Integer atBatId = rs.getInt("at_bat_id");

                AtBatDTO atBatDTO;

                if (!atBatIds.contains(atBatId)) {

                    atBatDTO = processAtBatDTO(rs);
                    inningDTO.getAtBats().add(atBatDTO);
                } else {
                    atBatDTO = inningDTO.getAtBats().get(inningDTO.getAtBats().size()-1);

                    Optional<Player> optionalPlayerScored = getOptionalAdditionalPlayerScored(rs);
                    Optional<Player> optionalPlayerOut = getOptionalAdditionalPlayerOut(rs);

                    optionalPlayerScored.ifPresent(player -> atBatDTO.getRuns().add(player));
                    optionalPlayerOut.ifPresent(player -> atBatDTO.getOuts().add(player));
                }

                atBatIds.add(atBatId);
            }

        }

        List<InningDTO> innings = inningMap.values().stream().toList();

        return Scoresheet.builder().innings(innings).build();
    }

    private AtBatDTO processAtBatDTO(ResultSet rs) throws SQLException {

        Boolean single = rs.getBoolean("single");
        Boolean dubble = rs.getBoolean("double");
        Boolean triple = rs.getBoolean("triple");
        Boolean homerun = rs.getBoolean("homerun");
        Boolean walk = rs.getBoolean("walk");
        Boolean skip = rs.getString("scoring").equals("Skip");

        ResultENUM result;

        if (homerun) {
            result = ResultENUM.HOMERUN;
        } else if (triple) {
            result = ResultENUM.TRIPLE;
        } else if (dubble) {
            result = ResultENUM.DOUBLE;
        } else if (triple) {
            result = ResultENUM.TRIPLE;
        } else if (single) {
            result = ResultENUM.SINGLE;
        } else if (walk) {
            result = ResultENUM.WALK;
        } else if (skip) {
            result = ResultENUM.SKIP;
        } else {
            result = ResultENUM.OUT;
        }

        return AtBatDTO.builder()
                .atBatId(rs.getInt("at_bat_id"))
                .player(
                        Player.builder()
                                .id(rs.getInt("player_id"))
                                .firstName(rs.getString("ab.first_name"))
                                .lastName(rs.getString("ab.last_name"))
                                .build()
                        )
                .inningIndex(rs.getInt("inning_index"))
                .result(result)
                .scoring(rs.getString("scoring"))
                .region(RegionENUM.fromString(rs.getString("region")))
                .launchAngle(LaunchAngleENUM.fromString(rs.getString("launch_angle")))
                .exitVelocity(rs.getInt("exit_velocity"))
                .baserunners(Baserunners.builder()
                        .first(rs.getInt("first_base") != 0 ?
                                Player.builder()
                                        .id(rs.getInt("first_base"))
                                        .firstName(rs.getString("p1b.first_name"))
                                        .lastName(rs.getString("p1b.last_name"))
                                        .build() : null

                                )
                        .second(rs.getInt("second_base") != 0 ?
                                Player.builder()
                                        .id(rs.getInt("second_base"))
                                        .firstName(rs.getString("p2b.first_name"))
                                        .lastName(rs.getString("p2b.last_name"))
                                        .build() : null

                        )
                        .third(rs.getInt("third_base") != 0 ?
                                Player.builder()
                                        .id(rs.getInt("third_base"))
                                        .firstName(rs.getString("p3b.first_name"))
                                        .lastName(rs.getString("p3b.last_name"))
                                        .build() : null

                        )
                        .build())
                .outs(
                        rs.getInt("player_out") != 0 ?
                                new HashSet<>(Set.of(
                                        Player.builder()
                                                .id(rs.getInt("player_out"))
                                                .firstName(rs.getString("p_out.first_name"))
                                                .lastName(rs.getString("p_out.last_name"))
                                                .build()
                                ))
                                : Set.of()
                )
                .runs(
                        rs.getInt("player_scored") != 0 ?
                                new HashSet<>(Set.of(
                                        Player.builder()
                                                .id(rs.getInt("player_scored"))
                                                .firstName(rs.getString("p_scored.first_name"))
                                                .lastName(rs.getString("p_scored.last_name"))
                                                .build()
                                ))
                                : Set.of()
                )
                .build();
    }

    private Optional<Player> getOptionalAdditionalPlayerScored(ResultSet rs) throws SQLException {

        return rs.getInt("player_scored") != 0 ?
                        Optional.of(Player.builder()
                                .id(rs.getInt("player_scored"))
                                .firstName(rs.getString("p_scored.first_name"))
                                .lastName(rs.getString("p_scored.last_name"))
                                .build())
                : Optional.empty();
    }

    private Optional<Player> getOptionalAdditionalPlayerOut(ResultSet rs) throws SQLException {

        return rs.getInt("player_out") != 0 ?
                        Optional.of(Player.builder()
                                .id(rs.getInt("player_out"))
                                .firstName(rs.getString("p_out.first_name"))
                                .lastName(rs.getString("p_out.last_name"))
                                .build())
                :Optional.empty();
    }
}
