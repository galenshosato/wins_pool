package gssato.wins_pool.data;

import gssato.wins_pool.data.mappers.UserMapper;
import gssato.wins_pool.models.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserJdbcTemplateRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> findAllUsers() {
        final String sql = "select user_id, first_name, last_name, email, password, is_deleted, is_admin, money_owed, team_id from user;";
        return jdbcTemplate.query(sql, new UserMapper());
    }

    public List<User> findAllCurrentUsers() {
        final String sql = "select user_id, first_name, last_name, email, password, is_deleted, is_admin, money_owed, team_id from user where is_deleted = 1;";
        return jdbcTemplate.query(sql, new UserMapper());
    }

    
}
