package gssato.wins_pool.data;

import gssato.wins_pool.data.mappers.DraftPickMapper;
import gssato.wins_pool.models.DraftPick;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
@Repository
public class DraftPickJdbcTemplateRepository implements DraftPickRepository {

    private final JdbcTemplate jdbcTemplate;

    public DraftPickJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<DraftPick> findAllDraftPicks() {
        final String sql = "select draft_pick_id, pick_number from draft_pick;";
        return jdbcTemplate.query(sql, new DraftPickMapper());
    }

    @Override
    public DraftPick findDraftPickByNumber(int pickNumber) {
        final String sql = "select draft_pick_id, pick_number from draft_pick "
                + "where pick_number = ?;";
        return jdbcTemplate.query(sql, new DraftPickMapper(), pickNumber).stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public DraftPick addDraftPick(DraftPick draftPick) {
        final String sql = "insert into draft_pick (pick_number) values (?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, draftPick.getPickNumber());
            return ps;
        }, keyHolder);

        if (rowsAffected <= 0) {
            return null;
        }
        draftPick.setDraftPickId(keyHolder.getKey().intValue());
        return draftPick;
    }

    @Override
    public boolean deleteByDraftPickId(int draftPickId) {
        jdbcTemplate.update("delete from draft where draft_pick_id = ?;", draftPickId);
        return jdbcTemplate.update("delete from draft_pick where draft_pick_id = ?;", draftPickId) > 0;
    }
}
