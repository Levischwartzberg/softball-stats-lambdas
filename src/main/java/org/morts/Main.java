package org.morts;

import org.morts.dto.Player;
import org.morts.lambdas.PlayerLambda;
import org.morts.lambdas.PlayerLifetimeStatsLambda;
import org.morts.util.Statline;

import java.sql.SQLException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        PlayerLifetimeStatsLambda playerLifetimeStatsLambda = new PlayerLifetimeStatsLambda();
        Statline statline = playerLifetimeStatsLambda.getPlayerLifetimeStats(2);
        System.out.println(statline.getSlg());
    }
}