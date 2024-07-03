package gssato.wins_pool.controllers;

import gssato.wins_pool.domain.Result;
import gssato.wins_pool.domain.TeamService;
import gssato.wins_pool.models.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"})
@RequestMapping("/api/teams")
public class TeamController {

    @Autowired
    TeamService service;

    @GetMapping
    public List<Team> findAllTeams() {
        return service.findAllTeams();
    }

    @GetMapping("/{league}")
    public List<Team> findAllTeamsByLeague(@PathVariable String league) {
        return service.findAllTeamsByLeague(league);
    }

    @GetMapping("/{teamId}")
    public Team findTeamById(@PathVariable int teamId) {
        return service.findTeamById(teamId);
    }

    @PostMapping
    public ResponseEntity<Object> addTeam(@RequestBody Team team) {
        Result<Team> result = service.addTeam(team);
        if (result.isSuccess()) {
            return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
        }
        return ErrorResponse.build(result);
    }

    @PutMapping("/{teamId}")
    public ResponseEntity<Object> updateTeam(@PathVariable int teamId, @RequestBody Team team) {
        if (teamId != team.getTeamId()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Result<Team> result = service.updateTeam(team);
        if (result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return ErrorResponse.build(result);
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<Void> deleteTeam(@PathVariable int teamId) {
        if (service.deleteById(teamId)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
