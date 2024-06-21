package gssato.wins_pool.domain;

import gssato.wins_pool.data.TeamRepository;
import gssato.wins_pool.models.Team;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.when;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class TeamServiceTest {

    @Autowired
    TeamService service;

    @MockBean
    TeamRepository repository;

    @Test
    void shouldNotAddWhenIdExists() {
        Team team = makeTeam();
        team.setTeamId(2);
        Result<Team> actual = service.addTeam(team);
        assertEquals(ResultType.INVALID, actual.getType());
    }

    @Test
    void shouldNotAddWhenLocationIsNull() {
        Team team = makeTeam();
        team.setLocation(null);
        Result<Team> actual = service.addTeam(team);
        assertEquals(ResultType.INVALID, actual.getType());
    }

    @Test
    void shouldNotAddWhenColorIsEmpty() {
        Team team = makeTeam();
        team.setColor("");
        Result<Team> actual = service.addTeam(team);
        assertEquals(ResultType.INVALID, actual.getType());
    }

    @Test
    void shouldNotAddWhenAltColorIsEmpty() {
        Team team = makeTeam();
        team.setAltColor("");
        Result<Team> actual = service.addTeam(team);
        assertEquals(ResultType.INVALID, actual.getType());
    }

    @Test
    void shouldNotAddWhenTeamIsNull() {
        Result<Team> actual = service.addTeam(null);
        assertEquals(ResultType.INVALID, actual.getType());

    }

    @Test
    void shouldAddTeam() {
        Team team = makeTeam();
        Team mockTeam = makeTeam();
        mockTeam.setTeamId(1);

        when(repository.addTeam(team)).thenReturn(mockTeam);

        Result<Team> actual = service.addTeam(team);
        assertEquals(ResultType.SUCCESS, actual.getType());
        assertEquals(mockTeam, actual.getPayload());
    }

    @Test
    void shouldNotUpdateWhenInvalid() {
        Team team = makeTeam();
        Result<Team> actual = service.updateTeam(team);
        assertEquals(ResultType.INVALID, actual.getType());

        team = makeTeam();
        team.setTeamId(1);
        team.setLocation(null);
        actual = service.updateTeam(team);
        assertEquals(ResultType.INVALID, actual.getType());

        team = makeTeam();
        team.setTeamId(1);
        team.setTeamName("");
        actual = service.updateTeam(team);
        assertEquals(ResultType.INVALID, actual.getType());

        team = makeTeam();
        team.setTeamId(1);
        team.setColor("     ");
        actual = service.updateTeam(team);
        assertEquals(ResultType.INVALID, actual.getType());

        team = makeTeam();
        team.setTeamId(1);
        team.setAltColor(null);
        actual = service.updateTeam(team);
        assertEquals(ResultType.INVALID, actual.getType());
    }

    @Test
    void shouldNotUpdateWhenInvalidId() {
        Team team = makeTeam();
        team.setTeamId(50);

        when(repository.updateTeam(team)).thenReturn(false);
        Result<Team> actual = service.updateTeam(team);
        assertEquals(ResultType.NOT_FOUND, actual.getType());
    }

    @Test
    void shouldNotDeleteWhenNotFound() {
        when(repository.deleteTeamById(4)).thenReturn(false);
        assertFalse(service.deleteById(4));
    }

    @Test
    void shouldDelete() {
        when(repository.deleteTeamById(1)).thenReturn(true);
        assertTrue(service.deleteById(1));
    }


    Team makeTeam() {
        Team team = new Team();

        team.setLocation("New England");
        team.setTeamName("Patriots");
        team.setColor("Red");
        team.setAltColor("White");
        return team;
    }
}