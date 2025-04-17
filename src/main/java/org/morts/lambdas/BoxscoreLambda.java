package org.morts.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.morts.domain.Opponent;
import org.morts.domain.Player;
import org.morts.domain.Result;
import org.morts.dto.Boxscore;
import org.morts.dto.GameInfoDTO;
import org.morts.dto.PlayerStatline;
import org.morts.dto.Statline;
import org.morts.util.StatCalculatorUtil;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class BoxscoreLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    String dbUrl = System.getenv("DB_URL");
    String dbUser = System.getenv("DB_USER");
    String dbPassword = System.getenv("DB_PASSWORD");

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        String resultId = event.getPathParameters().get("resultId");

        if (resultId == null) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody("Missing resultId parameter");
        }

        Boxscore boxscore;
        try {
            boxscore = getBoxscore(Integer.valueOf(resultId));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(boxscore.toString());
    }

    public BoxscoreLambda() {
    }

    public Boxscore getBoxscore(Integer gameInfoId) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPassword);
        PreparedStatement preparedStatement = connection.prepareStatement("select at_bats_with_rbi.player_id as player_id, at_bats_with_rbi.first_name, at_bats_with_rbi.last_name, sum(at_bats_with_rbi.ab) as at_bats, sum(at_bats_with_rbi.hit) as hits, sum(at_bats_with_rbi.single) as singles, sum(at_bats_with_rbi.`double`) as doubles, sum(at_bats_with_rbi.triple) as triples, sum(at_bats_with_rbi.homerun) as homeruns, sum(at_bats_with_rbi.walk) as walks, sum(at_bats_with_rbi.rbi) as rbi, runs\n" +
                "from (select p.first_name, p.last_name, ab, hit, single, `double`, triple, homerun, walk, at_bats.player_id, abr.rbi as rbi from at_bats\n" +
                "left join innings i on at_bats.inning_id = i.inning_id\n" +
                "left join players p on at_bats.player_id = p.player_id\n" +
                "left join (select count(*) as rbi, at_bat_id from at_bat_runs group by at_bat_id) abr on at_bats.at_bat_id = abr.at_bat_id\n" +
                "where i.game_info_id = ?) as at_bats_with_rbi\n" +
                "left join (select count(*) as runs, at_bat_runs.player_id from at_bat_runs\n" +
                "inner join at_bats a on at_bat_runs.at_bat_id = a.at_bat_id\n" +
                "inner join innings i2 on a.inning_id = i2.inning_id\n" +
                "where i2.game_info_id = ?\n" +
                "group by at_bat_runs.player_id) abrs on at_bats_with_rbi.player_id = abrs.player_id\n" +
                "group by at_bats_with_rbi.first_name, at_bats_with_rbi.last_name;");
        preparedStatement.setInt(1, gameInfoId);
        preparedStatement.setInt(2, gameInfoId);
        ResultSet boxscoreRS = preparedStatement.executeQuery();

        GameInfoDTO gameInfo = null;

        List<PlayerStatline> playerStatlines = new ArrayList<>();
        while (boxscoreRS.next()) {

            int hits = boxscoreRS.getInt("hits");
            int singles = boxscoreRS.getInt("singles");
            int doubles = boxscoreRS.getInt("doubles");
            int triples = boxscoreRS.getInt("triples");
            int homeruns = boxscoreRS.getInt("homeruns");
            int walks = boxscoreRS.getInt("walks");
            int atBats = boxscoreRS.getInt("at_bats");

            PlayerStatline playerStatline = PlayerStatline.builder()
                    .player(
                            Player.builder()
                                    .id(boxscoreRS.getInt("player_id"))
                                    .firstName(boxscoreRS.getString("first_name"))
                                    .lastName(boxscoreRS.getString("last_name"))
                                    .build()
                    )
                    .statline(
                            Statline.builder()
                                    .atBats(atBats)
                                    .hits(hits)
                                    .singles(singles)
                                    .doubles(doubles)
                                    .triples(triples)
                                    .homeruns(homeruns)
                                    .walks(walks)
                                    .runs(boxscoreRS.getInt("runs"))
                                    .rbi(boxscoreRS.getInt("rbi"))
                                    .avg((double) hits / (double) atBats)
                                    .obp(StatCalculatorUtil.calculateOBP(hits, atBats, walks))
                                    .slg(StatCalculatorUtil.calculateSLG(singles, doubles, triples, homeruns, atBats))
                                    .ops(StatCalculatorUtil.calculateOBP(hits, atBats, walks) + StatCalculatorUtil.calculateSLG(singles, doubles, triples, homeruns, atBats))
                                    .build()
                    )
                    .build();

            playerStatlines.add(playerStatline);
        }

        PreparedStatement preparedStatement2 = connection.prepareStatement("select game_info.*, opponents.team_name as opponent_team_name from game_info\n" +
                "left join opponents on game_info.opponent_id = opponents.opponent_id\n" +
                "where game_info_id = ?;");
        preparedStatement2.setInt(1, gameInfoId);
        ResultSet gameInfoRS = preparedStatement2.executeQuery();
        while (gameInfoRS.next()) {

            gameInfo = GameInfoDTO.builder()
                    .gameInfoId(gameInfoRS.getInt("game_info_id"))
                    .home(determineHomeAway(gameInfoRS))
                    .runsFor(gameInfoRS.getInt("runs_for"))
                    .runsAgainst(gameInfoRS.getInt("runs_against"))
                    .opponent(Opponent.builder()
                            .id(gameInfoRS.getInt("opponent_id"))
                            .teamName("opponent_team_name")
                            .build())
                    .field(gameInfoRS.getString("field"))
                    .temperature(gameInfoRS.getInt("temperature"))
                    .weatherConditions(Optional.ofNullable(gameInfoRS.getString("weather_conditions")).map(conditions -> conditions.split(",")).map(Arrays::asList).orElse(null))
                    .gameNotes(gameInfoRS.getString("game_notes"))
                    .date(gameInfoRS.getTimestamp("game_date_time"))
                    .build();
        }

        return Boxscore.builder()
                .gameInfo(gameInfo)
                .playerStatlines(playerStatlines)
                .build();
    }

    private Boolean determineHomeAway(ResultSet rs) throws SQLException {

        Boolean homeAway = rs.getBoolean("home_away");
        if (rs.wasNull()) {
            return null;
        } else {
            return homeAway;
        }
    }
}
