package gssato.wins_pool.data;

import gssato.wins_pool.models.Team;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TeamRepository {
    List<Team> findAllTeams();

    List<Team> findAllTeamsByLeague(String league);

    Team findTeamById(int teamId);

    Team addTeam(Team team);

    boolean updateTeam(Team team);
    @Transactional
    boolean deleteTeamById(int teamId);
}
