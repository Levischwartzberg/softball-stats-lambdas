package org.morts;

import org.morts.util.Statline;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        PlayerLifetimeStatsLambda playerLifetimeStatsLambda = new PlayerLifetimeStatsLambda();
        Statline statline = playerLifetimeStatsLambda.getPlayerLifetimeStats(2);
        System.out.println(statline.getSlg());
    }
}