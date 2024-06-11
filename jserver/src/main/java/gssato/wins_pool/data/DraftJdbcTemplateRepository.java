package gssato.wins_pool.data;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DraftJdbcTemplateRepository {

    private final JdbcTemplate jdbcTemplate;

    public DraftJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


}
