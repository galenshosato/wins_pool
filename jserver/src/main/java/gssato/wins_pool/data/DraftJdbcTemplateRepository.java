package gssato.wins_pool.data;

import gssato.wins_pool.data.mappers.DraftMapper;
import gssato.wins_pool.models.Draft;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DraftJdbcTemplateRepository {

    private final JdbcTemplate jdbcTemplate;

    public DraftJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Draft> findAllDraftPicksByYear(int year) {
        final String sql = "select * "
                + "from draft d "
                + "join user u on d.user_id = u.user_id "
                + "join year y on d.year_id = y.year_id "
                + "join draft_pick dp on d.draft_pick_id = dp.draft_pick_id "
                + "left join team t on d.team_id = t.team_id "
                + "where y.year_number = ?;";
        return jdbcTemplate.query(sql, new DraftMapper(), year);
    }

    public List<Draft> findAllDraftPicksByUserAndYear(int year, int userId) {
        final String sql = "select * "
                + "from draft d "
                + "join user u on d.user_id = u.user_id "
                + "join year y on d.year_id = y.year_id "
                + "join draft_pick dp on d.draft_pick_id = dp.draft_pick_id "
                + "left join team t on d.team_id = t.team_id "
                + "where y.year_number = ? and u.user_id = ?;";
        return jdbcTemplate.query(sql, new DraftMapper(), year, userId);
    }

    //TODO Write createDraftPick function
    //TODO Write updateDraftPickTeam function
    
}
