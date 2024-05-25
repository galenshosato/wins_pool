package gssato.wins_pool.data.mappers;

import gssato.wins_pool.models.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet resultSet, int i) throws SQLException {
        User user = new User();
        user.setUserId(resultSet.getInt("user_id"));
        user.setFirstName(resultSet.getString("first_name"));
        user.setLastName(resultSet.getString("last_name"));
        user.setEmail(resultSet.getString("email"));
        user.setPassword(resultSet.getString("password"));
        user.setDeleted(resultSet.getBoolean("is_deleted"));
        user.setAdmin(resultSet.getBoolean("is_admin"));
        user.setMoneyOwed(resultSet.getBigDecimal("money_owed"));

        TeamMapper teamMapper = new TeamMapper();
        user.setFavoriteTeam(teamMapper.mapRow(resultSet,i));

        return user;
    }
}
