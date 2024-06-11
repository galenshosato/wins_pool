package gssato.wins_pool.data.mappers;

import gssato.wins_pool.models.Draft;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DraftMapper implements RowMapper<Draft> {

    @Override
    public Draft mapRow(ResultSet resultSet, int i) throws SQLException {
        Draft draft = new Draft();

        draft.setDraftId(resultSet.getInt("draft_id"));

        UserMapper userMapper = new UserMapper();
        draft.setUser(userMapper.mapRow(resultSet, i));

        YearMapper yearMapper = new YearMapper();
        draft.setYear(yearMapper.mapRow(resultSet, i));

        DraftPickMapper draftPickMapper = new DraftPickMapper();
        draft.setDraftPick(draftPickMapper.mapRow(resultSet, i));

        TeamMapper teamMapper = new TeamMapper();
        try {
            draft.setTeam(teamMapper.mapRow(resultSet, i));
        } catch (SQLException ex) {
            draft.setTeam(null);
        }


        return draft;
    }
}
