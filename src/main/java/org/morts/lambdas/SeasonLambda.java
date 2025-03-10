package org.morts.lambdas;

import org.morts.domain.Season;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeasonLambda {
    String dbUrl = System.getenv("DB_URL");
    String dbUser = System.getenv("DB_USER");
    String dbPassword = System.getenv("DB_PASSWORD");

    public SeasonLambda() {
    }

    public List<Season> getSeasons() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPassword);
        PreparedStatement preparedStatement = connection.prepareStatement("select * from season");
        ResultSet rs = preparedStatement.executeQuery();
        List<Season> seasons = new ArrayList();

        while(rs.next()) {
            Season season = Season.builder()
                    .id(rs.getInt("id"))
                    .session(rs.getString("session"))
                    .year(rs.getInt("year"))
                    .build();
            seasons.add(season);
        }

        return seasons;
    }
}
