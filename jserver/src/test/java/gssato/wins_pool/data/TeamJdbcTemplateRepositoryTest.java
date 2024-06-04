package gssato.wins_pool.data;

import gssato.wins_pool.models.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class TeamJdbcTemplateRepositoryTest {

    @Autowired
    TeamRepository repository;

    @Autowired
    KnownGoodState knownGoodState;

    @BeforeEach
    void setup() {knownGoodState.set();}

    @Test
    void shouldFindAllTeams() {
        List<Team> teams = repository.findAllTeams();
        assertNotNull(teams);
        assertTrue(teams.size() >= 3);
        assertEquals("Bears", teams.get(2).getTeamName());
    }

    @Test
    void shouldFindAllTeamsInAFCAndNFC() {
        List<Team> afcTeams = repository.findAllTeamsByLeague("AFC");
        assertNotNull(afcTeams);
        assertFalse(afcTeams.isEmpty());
        assertEquals("Patriots", afcTeams.get(0).getTeamName());

        List<Team> nfcTeams = repository.findAllTeamsByLeague("NFC");
        assertNotNull(nfcTeams);
        assertTrue(nfcTeams.size() >= 2);
        assertEquals("red",nfcTeams.get(0).getColor());
        assertEquals("Chicago", nfcTeams.get(1).getLocation());
    }

    @Test
    void shouldFindTeamById() {
        Team team = repository.findTeamById(2);
        assertNotNull(team);
        assertEquals("New England", team.getLocation());
        assertEquals("Patriots", team.getTeamName());
        assertEquals("blue", team.getColor());
        assertEquals("white", team.getAltColor());
    }

    @Test
    void shouldNotFindTeamById() {
        Team team = repository.findTeamById(100);
        assertNull(team);
    }

    @Test
    void shouldAddTeam() {
        Team newTeam = new Team();
        newTeam.setLocation("Atlanta");
        newTeam.setTeamName("Falcons");
        newTeam.setColor("crimson");
        newTeam.setAltColor("white");
        newTeam.setLeague("NFC");

        Team actual = repository.addTeam(newTeam);
        assertNotNull(actual);
        assertEquals(5, actual.getTeamId());

        Team newTeam2 = new Team();
        newTeam2.setLocation("Arizona");
        newTeam2.setTeamName("Cardinals");
        newTeam2.setColor("red");
        newTeam2.setAltColor("black");
        newTeam2.setLeague(null);

        Team actual2 = repository.addTeam(newTeam2);
        assertEquals(6, actual2.getTeamId());
        assertNull(actual2.getLeague());
    }

    @Test
    void shouldUpdateTeam() {
        Team teamToUpdate = new Team();
        teamToUpdate.setLocation("Denver");
        teamToUpdate.setTeamName("Broncos");
        teamToUpdate.setColor("blue");
        teamToUpdate.setAltColor("orange");
        teamToUpdate.setLeague("AFC");
        teamToUpdate.setTeamId(4);
        assertTrue(repository.updateTeam(teamToUpdate));

    }

    @Test
    void shouldNotUpdateTeam() {
        Team teamToUpdate = new Team();
        teamToUpdate.setLocation("Denver");
        teamToUpdate.setTeamName("Broncos");
        teamToUpdate.setColor("blue");
        teamToUpdate.setAltColor("orange");
        teamToUpdate.setLeague("AFC");
        teamToUpdate.setTeamId(10);
        assertFalse(repository.updateTeam(teamToUpdate));
    }

    @Test
    void shouldDeleteTeam() {
        assertTrue(repository.deleteTeamById(6));
    }

    @Test
    void shouldNotDeleteTeamInvalidId() {
        assertFalse(repository.deleteTeamById(100));
    }





}