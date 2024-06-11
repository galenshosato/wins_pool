package gssato.wins_pool.data;

import com.mysql.cj.conf.HostsListView;
import gssato.wins_pool.data.mappers.TeamMapper;
import gssato.wins_pool.models.Team;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class TeamJdbcTemplateRepository implements TeamRepository {

    private final JdbcTemplate jdbcTemplate;

    public TeamJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Team> findAllTeams() {
        final String sql = "select team_id, location, team_name, color, alt_color, league from team;";
        return jdbcTemplate.query(sql, new TeamMapper());
    }

    @Override
    public List<Team> findAllTeamsByLeague(String league) {
        final String sql = "select team_id, location, team_name, color, alt_color, league "
                + "from team "
                + "where league = ?;";
        return jdbcTemplate.query(sql, new TeamMapper(), league);
    }

    @Override
    public Team findTeamById(int teamId) {
        final String sql = "select team_id, location, team_name, color, alt_color, league "
                + "from team "
                + "where team_id = ?;";
        return jdbcTemplate.query(sql, new TeamMapper(), teamId).stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public Team addTeam(Team team) {
        final String sql = "insert into team (location, team_name, color, alt_color, league) "
                + "values (?,?,?,?,?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, team.getLocation());
            ps.setString(2, team.getTeamName());
            ps.setString(3, team.getColor());
            ps.setString(4, team.getAltColor());
            ps.setString(5, team.getLeague() == null ? null : team.getLeague());
            return ps;
        }, keyHolder);

        if (rowsAffected <= 0) {
            return null;
        }

        team.setTeamId(keyHolder.getKey().intValue());
        return team;
    }

    @Override
    public boolean updateTeam(Team team) {
        final String sql = "update team set "
                + "location = ?, "
                + "team_name = ?, "
                + "color = ?, "
                + "alt_color = ?, "
                + "league = ? "
                + "where team_id = ?;";

        return jdbcTemplate.update(sql,
                team.getLocation(),
                team.getTeamName(),
                team.getColor(),
                team.getAltColor(),
                team.getLeague(),
                team.getTeamId()) > 0;
    }

    @Override
    @Transactional
    public boolean deleteTeamById(int teamId) {
        jdbcTemplate.update("delete from draft where team_id = ?;", teamId);
        jdbcTemplate.update("delete from user where team_id = ?;", teamId);
        return jdbcTemplate.update("delete from team where team_id = ?;", teamId) > 0;
    }
}
