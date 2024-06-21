package gssato.wins_pool.domain;

import gssato.wins_pool.data.TeamRepository;
import gssato.wins_pool.models.Team;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamService {

    private final TeamRepository repository;

    public TeamService(TeamRepository repository) {
        this.repository = repository;
    }

    public List<Team> findAllTeams() {return repository.findAllTeams();}

    public List<Team> findAllTeamsByLeague(String league) {
        return repository.findAllTeamsByLeague(league);
    }

    public Team findTeamById(int teamId) {
        return repository.findTeamById(teamId);
    }

    public Result<Team> addTeam(Team team) {
        Result<Team> result = validate(team);

        if (!result.isSuccess()) {
            return result;
        }

        if (team.getTeamId() != 0) {
            result.addMessage("teamId cannot be set for an `add` operation", ResultType.INVALID);
            return result;
        }

        team = repository.addTeam(team);
        result.setPayload(team);
        return result;
    }

    public Result<Team> updateTeam(Team team) {
        Result<Team> result = validate(team);
        if (!result.isSuccess()) {
            return result;
        }

        if (team.getTeamId() <= 0) {
            result.addMessage("The team's id must be set for the `update` operation", ResultType.INVALID);
            return result;
        }

        if (!repository.updateTeam(team)) {
            String msg = String.format("teamId: %s was not found", team.getTeamId());
            result.addMessage(msg, ResultType.NOT_FOUND);
        }

        return result;
    }

    public boolean deleteById(int teamId) { return repository.deleteTeamById(teamId);}

    private Result<Team> validate(Team team) {
        Result<Team> result = new Result<>();

        if (team == null) {
            result.addMessage("team cannot be null", ResultType.INVALID);
            return result;
        }

        if (Validations.isNullOrBlank(team.getLocation())) {
            result.addMessage("location is required", ResultType.INVALID);
        }

        if (Validations.isNullOrBlank(team.getTeamName())) {
            result.addMessage("team name is required", ResultType.INVALID);
        }

        if (Validations.isNullOrBlank(team.getColor())) {
            result.addMessage("All teams must have a color", ResultType.INVALID);
        }

        if (Validations.isNullOrBlank(team.getAltColor())) {
            result.addMessage("All teams must have an alternative color", ResultType.INVALID);
        }

        return result;

    }
}
