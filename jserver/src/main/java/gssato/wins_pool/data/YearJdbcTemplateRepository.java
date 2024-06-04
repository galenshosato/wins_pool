package gssato.wins_pool.data;

import gssato.wins_pool.data.mappers.YearMapper;
import gssato.wins_pool.models.Year;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class YearJdbcTemplateRepository implements YearRepository {

    private final JdbcTemplate jdbcTemplate;

    public YearJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Year> findAll() {
        final String sql = "select year_id, year_number from year;";
        return jdbcTemplate.query(sql, new YearMapper());
    }

    @Override
    public Year findYearByYearNumber(int yearNumber) {
        final String sql = "select year_id, year_number "
                + "from year "
                + "where year_number = ?;";
        return jdbcTemplate.query(sql, new YearMapper(), yearNumber).stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public Year addYear(Year year) {
        final String sql = "insert into year (year_number) "
                + "values (?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, year.getYearNumber());
            return ps;
        }, keyHolder);

        if (rowsAffected <= 0) {
            return null;
        }

        year.setYearId(keyHolder.getKey().intValue());
        return year;
    }

    @Override
    public boolean updateYear(Year year) {
        final String sql = "update year set "
                + "year_number = ? "
                + "where year_id = ?;";
        return jdbcTemplate.update(sql,
                year.getYearNumber(),
                year.getYearId()) > 0;
    }

    @Override
    @Transactional
    public boolean deleteYearById(int yearId) {
        jdbcTemplate.update("delete from draft where year_id = ?;", yearId);
        return jdbcTemplate.update("delete from year where year_id = ?;", yearId) > 0;
    }
}
