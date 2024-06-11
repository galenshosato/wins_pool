package gssato.wins_pool.data;

import gssato.wins_pool.data.mappers.TeamMapper;
import gssato.wins_pool.data.mappers.UserMapper;
import gssato.wins_pool.models.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class UserJdbcTemplateRepository implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> findAllUsers() {
        final String sql = "select user_id, first_name, last_name, email, is_deleted, is_admin, money_owed, team_id from user;";
        return jdbcTemplate.query(sql, new UserMapper());
    }

    @Override
    public List<User> findAllCurrentUsers() {
        final String sql = "select user_id, first_name, last_name, email, is_deleted, is_admin, money_owed, team_id from user where is_deleted = 0;";
        return jdbcTemplate.query(sql, new UserMapper());
    }

    @Override
    @Transactional
    public User findUserByEmail(String email) {
        final String sql = "select user_id, first_name, last_name, email, password, is_deleted, is_admin, money_owed, team_id from user where email = ?;";
        User result = jdbcTemplate.query(sql, new UserMapper(), email).stream().findFirst().orElse(null);

        if (result != null) {
            addTeam(result);
        }
        return result;
    }

    @Override
    public User addUser(User user) {
        final String sql = "insert into user (first_name, last_name, email, password, team_id) "
                + "values (?,?,?,?,?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPassword());
            ps.setInt(5, user.getFavoriteTeam().getTeamId());
            return ps;
        }, keyHolder);

        if (rowsAffected <= 0) {
            return null;
        }

        user.setUserId(keyHolder.getKey().intValue());
        return user;
    }

    @Override
    public boolean updateUser(User user) {
        final String sql = "update user set "
                + "first_name = ?, "
                + "last_name = ?, "
                + "email = ?, "
                + "password = ?, "
                + "money_owed = ?, "
                + "team_id = ? "
                + "where user_id = ?;";
        return jdbcTemplate.update(sql,
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword(),
                user.getMoneyOwed(),
                user.getFavoriteTeam().getTeamId(),
                user.getUserId()) > 0;
    }

    @Override
    public boolean deleteUser(User user) {
        final String sql = "update user set "
                + "is_deleted = 1 "
                + "where user_id = ?;";
        return jdbcTemplate.update(sql, user.getUserId()) > 0;
    }



    private void addTeam(User user) {
        final String sql = "select team_id, location, team_name, color, alt_color, league "
                + "from team "
                + "where team_id = (select team_id from user where email = ?);";
        var team = jdbcTemplate.queryForObject(sql, new Object[]{user.getEmail()}, new TeamMapper());
        user.setFavoriteTeam(team);
    }





}
