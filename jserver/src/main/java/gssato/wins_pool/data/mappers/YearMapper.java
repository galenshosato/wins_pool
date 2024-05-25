package gssato.wins_pool.data.mappers;

import gssato.wins_pool.models.Year;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class YearMapper implements RowMapper<Year> {

    @Override
    public Year mapRow(ResultSet resultSet, int i) throws SQLException {
        Year year = new Year();

        year.setYearId(resultSet.getInt("year_id"));
        year.setYearNumber(resultSet.getInt("year_number"));

        return year;
    }
}
