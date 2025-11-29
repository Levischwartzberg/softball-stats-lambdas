package org.morts.util;

import org.morts.domain.Player;
import org.morts.domain.Season;
import org.morts.dto.PlayerStatline;
import org.morts.dto.SeasonStatline;
import org.morts.dto.Statline;
import org.morts.dto.YearlyStatline;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class StatCalculatorUtil {
    public static double calculateOBP(Integer hits, Integer atBats, Integer walks) {
        return ((double)hits + (double)walks) / ((double)atBats + (double)walks);
    }

    public static double calculateSLG(Integer singles, Integer doubles, Integer triples, Integer homeruns, Integer atBats) {
        return atBats != 0 ? (1.0 * (double)singles + 2.0 * (double)doubles + 3.0 * (double)triples + 4.0 * (double)homeruns) / (double)atBats : 0;
    }

    public static List<SeasonStatline> getSeasonStats(ResultSet rs) throws SQLException {

        List<SeasonStatline> seasonStatlines = new ArrayList<>();

        while (rs.next()) {
            int games = rs.getInt("games");
            int hits = rs.getInt("hits");
            int singles = rs.getInt("singles");
            int doubles = rs.getInt("doubles");
            int triples = rs.getInt("triples");
            int homeruns = rs.getInt("homeruns");
            int walks = rs.getInt("walks");
            int atBats = rs.getInt("at_bats");

            SeasonStatline seasonStatline = SeasonStatline.builder()
                    .statline(
                            Statline.builder()
                                    .games(games)
                                    .atBats(atBats)
                                    .hits(hits)
                                    .singles(singles)
                                    .doubles(doubles)
                                    .triples(triples)
                                    .homeruns(homeruns)
                                    .walks(walks)
                                    .rbi(rs.getInt("rbi"))
                                    .runs(rs.getInt("runs"))
                                    .avg((double) hits / (double) atBats)
                                    .obp(calculateOBP(hits, atBats, walks))
                                    .slg(calculateSLG(singles, doubles, triples, homeruns, atBats))
                                    .ops(calculateOBP(hits, atBats, walks) + calculateSLG(singles, doubles, triples, homeruns, atBats))
                                    .build()
                    )
                    .season(
                            Season.builder()
                                    .id(rs.getInt("season_id"))
                                    .session(rs.getString("session"))
                                    .year(rs.getInt("year"))
                                    .build()
                    ).build();
            seasonStatlines.add(seasonStatline);
        }
        return seasonStatlines;
    }

    public static List<YearlyStatline> getYearlyStats(ResultSet rs) throws SQLException {

        List<YearlyStatline> yearlyStatlines = new ArrayList<>();

        while (rs.next()) {
            int games = rs.getInt("games");
            int hits = rs.getInt("hits");
            int singles = rs.getInt("singles");
            int doubles = rs.getInt("doubles");
            int triples = rs.getInt("triples");
            int homeruns = rs.getInt("homeruns");
            int walks = rs.getInt("walks");
            int atBats = rs.getInt("at_bats");

            YearlyStatline yearlyStatline = YearlyStatline.builder()
                    .statline(
                            Statline.builder()
                                    .games(games)
                                    .atBats(atBats)
                                    .hits(hits)
                                    .singles(singles)
                                    .doubles(doubles)
                                    .triples(triples)
                                    .homeruns(homeruns)
                                    .walks(walks)
                                    .rbi(rs.getInt("rbi"))
                                    .runs(rs.getInt("runs"))
                                    .avg((double) hits / (double) atBats)
                                    .obp(calculateOBP(hits, atBats, walks))
                                    .slg(calculateSLG(singles, doubles, triples, homeruns, atBats))
                                    .ops(calculateOBP(hits, atBats, walks) + calculateSLG(singles, doubles, triples, homeruns, atBats))
                                    .wrcPlus(rs.getInt("wrc_plus"))
                                    .build()
                    )
                    .year(rs.getInt("year")).build();
            yearlyStatlines.add(yearlyStatline);
        }
        return yearlyStatlines;
    }

    public static List<PlayerStatline> getSeasonTeamStats(ResultSet rs) throws SQLException {

        List<PlayerStatline> playerStatlines = new ArrayList<>();

        boolean hasWrcPlus = false;
        try {
            rs.findColumn("wrc_plus");
            hasWrcPlus = true;
        } catch (SQLException e) {
            hasWrcPlus = false;
        }

        while (rs.next()) {
            int hits = rs.getInt("hits");
            int singles = rs.getInt("singles");
            int doubles = rs.getInt("doubles");
            int triples = rs.getInt("triples");
            int homeruns = rs.getInt("homeruns");
            int walks = rs.getInt("walks");
            int atBats = rs.getInt("at_bats");

            PlayerStatline playerStatline = PlayerStatline.builder()
                    .statline(
                            Statline.builder()
                                    .games(rs.getInt("games"))
                                    .atBats(atBats)
                                    .hits(hits)
                                    .singles(singles)
                                    .doubles(doubles)
                                    .triples(triples)
                                    .homeruns(homeruns)
                                    .walks(walks)
                                    .rbi(rs.getInt("rbi"))
                                    .runs(rs.getInt("runs"))
                                    .avg((double) hits / (double) atBats)
                                    .obp(calculateOBP(hits, atBats, walks))
                                    .slg(calculateSLG(singles, doubles, triples, homeruns, atBats))
                                    .ops(calculateOBP(hits, atBats, walks) + calculateSLG(singles, doubles, triples, homeruns, atBats))
                                    .wrcPlus(hasWrcPlus ? rs.getInt("wrc_plus") : null)
                                    .build()
                    )
                    .player(
                            Player.builder()
                                    .id(rs.getInt("player_id"))
                                    .lastName(rs.getString("last_name"))
                                    .firstName(rs.getString("first_name"))
                                    .build()
                    ).build();
            playerStatlines.add(playerStatline);
        }
        return playerStatlines;
    }

    public static Statline computeLifetimeStatline(ResultSet rs) throws SQLException {

        Statline statline = null;
        while (rs.next()) {
            int hits = rs.getInt("hits");
            int singles = rs.getInt("singles");
            int doubles = rs.getInt("doubles");
            int triples = rs.getInt("triples");
            int homeruns = rs.getInt("homeruns");
            int walks = rs.getInt("walks");
            int atBats = rs.getInt("at_bats");

            statline = Statline.builder()
                    .games(rs.getInt("games"))
                    .atBats(atBats)
                    .hits(hits)
                    .singles(singles)
                    .doubles(doubles)
                    .triples(triples)
                    .homeruns(homeruns)
                    .walks(walks)
                    .runs(rs.getInt("runs"))
                    .rbi(rs.getInt("rbi"))
                    .avg((double)hits / (double)atBats)
                    .obp(calculateOBP(hits, atBats, walks))
                    .slg(calculateSLG(singles, doubles, triples, homeruns, atBats))
                    .ops(calculateOBP(hits, atBats, walks) + calculateSLG(singles, doubles, triples, homeruns, atBats))
                    .wrcPlus(rs.getInt("wrc_plus"))
                    .build();
        }
        return statline;
    }

    private StatCalculatorUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
