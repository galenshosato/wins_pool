package gssato.wins_pool.data;

import gssato.wins_pool.data.mappers.DraftMapper;
import gssato.wins_pool.models.Draft;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class DraftJdbcTemplateRepository implements DraftRepository {

    private final JdbcTemplate jdbcTemplate;

    public DraftJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
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

    @Override
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


    @Override
    public Draft createDraftPick(Draft draftPick) {
        final String sql = "insert into draft (user_id, year_id, draft_pick_id) values (?,?,?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, draftPick.getUser().getUserId());
            ps.setInt(2, draftPick.getYear().getYearId());
            ps.setInt(3, draftPick.getDraftPick().getDraftPickId());
            return ps;
        }, keyHolder);

        if (rowsAffected <= 0) {
            return null;
        }

        draftPick.setDraftId(keyHolder.getKey().intValue());
        return draftPick;
    }

    @Override
    public boolean updateDraftPickWithTeam(Draft draftPick) {
        final String sql = "update draft set "
                + "team_id = ? "
                + "where draft_id = ?;";

        return jdbcTemplate.update(sql, draftPick.getTeam().getTeamId(), draftPick.getDraftId()) > 0;
    }



}
