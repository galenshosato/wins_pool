package gssato.wins_pool.data;

import gssato.wins_pool.models.Team;

import java.util.List;

public interface TeamRepository {
    List<Team> findAllTeams();

    List<Team> findAllTeamsByLeague(String league);

    Team findTeamById(int teamId);

    Team addTeam(Team team);

    boolean updateTeam(Team team);

    boolean deleteTeamById(int teamId);
}
