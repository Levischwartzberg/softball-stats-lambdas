package org.morts.lambdas;

import org.morts.util.StatCalculatorUtil;
import org.morts.util.Statline;

import java.sql.*;

public class PlayerLifetimeStatsLambda {
    String dbUrl = System.getenv("DB_URL");
    String dbUser = System.getenv("DB_USER");
    String dbPassword = System.getenv("DB_PASSWORD");

    public PlayerLifetimeStatsLambda() {
    }

    public Statline getPlayerLifetimeStats(Integer playerId) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPassword);
        PreparedStatement preparedStatement = connection.prepareStatement("select * from game g where player_id = " + playerId + ";");
        ResultSet rs = preparedStatement.executeQuery();
        return StatCalculatorUtil.computeStatlineFromGameIterator(rs);
    }
}
