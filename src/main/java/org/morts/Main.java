package org.morts;

import org.morts.dto.Player;
import org.morts.lambdas.PlayerLambda;

import java.sql.SQLException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        PlayerLambda playerLambda = new PlayerLambda();
        List<Player> playerList = playerLambda.getPlayers();
        System.out.println(playerList.get(0).getFirstName());
    }
}