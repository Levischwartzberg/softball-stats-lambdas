package org.morts.util;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class StatCalculatorUtil {
    public static double calculateOBP(Integer hits, Integer atBats, Integer walks) {
        return ((double)hits + (double)walks) / ((double)atBats + (double)walks);
    }

    public static double calculateSLG(Integer singles, Integer doubles, Integer triples, Integer homeruns, Integer atBats) {
        return (1.0 * (double)singles + 2.0 * (double)doubles + 3.0 * (double)triples + 4.0 * (double)homeruns) / (double)atBats;
    }

    public static Statline computeStatlineFromGameIterator(ResultSet rs) throws SQLException {
        Integer games = 0;
        Integer atBats = 0;
        Integer hits = 0;
        Integer singles = 0;
        Integer doubles = 0;
        Integer triples = 0;
        Integer homeruns = 0;
        Integer walks = 0;
        Integer runs = 0;

        Integer rbi;
        for(rbi = 0; rs.next(); rbi = rbi + rs.getInt("rbi")) {
            games = games + 1;
            atBats = atBats + rs.getInt("at_bats");
            hits = hits + rs.getInt("hits");
            singles = singles + rs.getInt("singles");
            doubles = doubles + rs.getInt("doubles");
            triples = triples + rs.getInt("triples");
            homeruns = homeruns + rs.getInt("homeruns");
            walks = walks + rs.getInt("walks");
            runs = runs + rs.getInt("runs");
        }

        return Statline.builder()
                .games(games)
                .atBats(atBats)
                .hits(hits)
                .singles(singles)
                .doubles(doubles)
                .triples(triples)
                .homeruns(homeruns)
                .walks(walks)
                .runs(runs)
                .rbi(rbi)
                .avg((double)hits / (double)atBats)
                .obp(calculateOBP(hits, atBats, walks))
                .slg(calculateSLG(singles, doubles, triples, homeruns, atBats))
                .ops(calculateOBP(hits, atBats, walks) + calculateSLG(singles, doubles, triples, homeruns, atBats)).build();
    }

    private StatCalculatorUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
