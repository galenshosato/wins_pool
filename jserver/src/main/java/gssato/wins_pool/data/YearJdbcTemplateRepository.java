package gssato.wins_pool.data;

import gssato.wins_pool.data.mappers.YearMapper;
import gssato.wins_pool.models.Year;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class YearJdbcTemplateRepository {

    private final JdbcTemplate jdbcTemplate;

    public YearJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Year> findAll() {
        final String sql = "select year_id, year_number from year;";
        return jdbcTemplate.query(sql, new YearMapper());
    }
}
