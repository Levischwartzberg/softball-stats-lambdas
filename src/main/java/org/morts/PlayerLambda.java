package org.morts;

import org.morts.dto.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerLambda {
    String dbUrl = System.getenv("DB_URL");
    String dbUser = System.getenv("DB_USER");
    String dbPassword = System.getenv("DB_PASSWORD");

    public PlayerLambda() {
    }

    public List<Player> getPlayers() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPassword);
        PreparedStatement preparedStatement = connection.prepareStatement("select * from player");
        ResultSet rs = preparedStatement.executeQuery();
        List<Player> players = new ArrayList();

        while(rs.next()) {
            Player player = Player.builder()
                    .id(rs.getInt("id"))
                    .firstName(rs.getString("first_name"))
                    .lastName(rs.getString("last_name"))
                    .batHand(rs.getString("bat_hand"))
                    .throwHand(rs.getString("throw_hand"))
                    .height(rs.getString("height"))
                    .weight(rs.getInt("weight")).build();
            players.add(player);
        }

        return players;
    }
}
