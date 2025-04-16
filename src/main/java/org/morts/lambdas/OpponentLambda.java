package org.morts.lambdas;

import org.morts.domain.Opponent;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OpponentLambda {
    String dbUrl = System.getenv("DB_URL");
    String dbUser = System.getenv("DB_USER");
    String dbPassword = System.getenv("DB_PASSWORD");

    public OpponentLambda() {
    }

    public List<Opponent> getOpponents() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPassword);
        PreparedStatement preparedStatement = connection.prepareStatement("select * from opponents");
        ResultSet rs = preparedStatement.executeQuery();
        List<Opponent> opponents = new ArrayList();

        while(rs.next()) {
            Opponent opponent = Opponent.builder()
                    .id(rs.getInt("opponent_id"))
                    .teamName(rs.getString("team_name"))
                    .build();
            opponents.add(opponent);
        }

        return opponents;
    }
}
