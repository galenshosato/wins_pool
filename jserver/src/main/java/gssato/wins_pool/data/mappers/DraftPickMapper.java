package gssato.wins_pool.data.mappers;

import gssato.wins_pool.models.DraftPick;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DraftPickMapper implements RowMapper<DraftPick> {

    @Override
    public DraftPick mapRow(ResultSet resultSet, int i) throws SQLException {
        DraftPick draftPick = new DraftPick();
        draftPick.setDraftPickId(resultSet.getInt("draft_pick_id"));
        draftPick.setPickNumber(resultSet.getInt("pick_number"));
        return draftPick;
    }
}
