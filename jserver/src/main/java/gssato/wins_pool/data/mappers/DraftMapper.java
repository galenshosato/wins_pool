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

        int teamId = resultSet.getInt("team_id");
        if (teamId == 0) {
            draft.setTeam(null);
        } else {
            TeamMapper teamMapper = new TeamMapper();
            draft.setTeam(teamMapper.mapRow(resultSet, i));
        }


        return draft;
    }
}
