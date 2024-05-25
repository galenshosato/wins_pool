package gssato.wins_pool.data.mappers;

import gssato.wins_pool.models.Team;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TeamMapper implements RowMapper<Team> {

    @Override
    public Team mapRow(ResultSet resultSet, int i) throws SQLException {
        Team team = new Team();

        team.setTeamId(resultSet.getInt("team_id"));
        team.setLocation(resultSet.getString("location"));
        team.setTeamName(resultSet.getString("team_name"));
        team.setColor(resultSet.getString("color"));
        team.setAltColor(resultSet.getString("alt_color"));
        return team;
    }
}
