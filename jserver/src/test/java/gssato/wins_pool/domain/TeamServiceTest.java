package gssato.wins_pool.domain;

import gssato.wins_pool.data.TeamRepository;
import gssato.wins_pool.models.Team;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
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

    Team makeTeam() {
        Team team = new Team();

        team.setLocation("New England");
        team.setTeamName("Patriots");
        team.setColor("Red");
        team.setAltColor("White");
        return team;
    }
}