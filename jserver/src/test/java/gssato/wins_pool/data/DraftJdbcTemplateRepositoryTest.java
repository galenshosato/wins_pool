package gssato.wins_pool.data;

import gssato.wins_pool.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class DraftJdbcTemplateRepositoryTest {

    @Autowired
    DraftRepository repository;

    @Autowired
    KnownGoodState knownGoodState;

    @BeforeEach
    void setup() {knownGoodState.set();}

    @Test
    void shouldReturnDraftPicksFor2023() {
        List<Draft> picks = repository.findAllDraftPicksByYear(2023);
        assertNotNull(picks);
        assertEquals("Luke", picks.get(0).getUser().getFirstName());
        assertEquals(2, picks.get(1).getDraftPick().getPickNumber());
        assertEquals("Chicago", picks.get(2).getTeam().getLocation());
    }

    @Test
    void shouldReturnDraftPickWithNullTeam() {
        List<Draft> picks = repository.findAllDraftPicksByYear(2024);
        assertNotNull(picks);
        assertEquals("NFC", picks.get(0).getTeam().getLeague());
        assertNull(picks.get(1).getTeam());

    }

    @Test
    void shouldNotReturnPicksInvalidYear() {
        List<Draft> picks = repository.findAllDraftPicksByYear(2030);
        assertTrue(picks.isEmpty());
    }

    @Test
    void shouldReturnPicksForLukeIn2023() {
        List<Draft> lukePicks = repository.findAllDraftPicksByUserAndYear(2023, 1);
        assertNotNull(lukePicks);
        assertEquals(1, lukePicks.size());
        assertEquals("Luke", lukePicks.get(0).getUser().getFirstName());
    }

    @Test
    void shouldReturnPicksForCalIn2023() {
        List<Draft> calPicks = repository.findAllDraftPicksByUserAndYear(2023, 2);
        assertNotNull(calPicks);
        assertEquals(2, calPicks.size());
        assertEquals(2, calPicks.get(0).getDraftPick().getPickNumber());
        assertEquals(3, calPicks.get(1).getDraftPick().getPickNumber());
    }

    @Test
    void shouldNotFindDraftObject() {
        Draft draftObject = repository.findDraftObjectById(10);
        assertNull(draftObject);
    }

    @Test
    void shouldFindDraftObjectById() {
        Draft draftObject = repository.findDraftObjectById(4);
        assertNotNull(draftObject);
        assertEquals("Luke", draftObject.getUser().getFirstName());
        assertEquals(2024, draftObject.getYear().getYearNumber());
        assertEquals("49ers", draftObject.getTeam().getTeamName());
    }

    @Test
    void shouldCreateNewDraft() {
        User user = new User();
        user.setUserId(3);
        Year year = new Year();
        year.setYearId(2);
        DraftPick draftPick = new DraftPick();
        draftPick.setDraftPickId(5);


        Draft draft = new Draft();
        draft.setUser(user);
        draft.setYear(year);
        draft.setDraftPick(draftPick);

        Draft actual = repository.createDraftPick(draft);
        assertNotNull(actual);
        assertEquals(6, actual.getDraftId());
        assertEquals(3, actual.getUser().getUserId());
        assertEquals(2, actual.getYear().getYearId());
        assertEquals(5, actual.getDraftPick().getDraftPickId());
        assertNull(actual.getTeam());

    }

    @Test
    void shouldUpdateDraftPickWithTeam() {
        Team team = new Team();
        team.setTeamId(3);
        List<Draft> draftObjects = repository.findAllDraftPicksByYear(2024);
        Draft draft = draftObjects.get(1);
        assertNull(draft.getTeam());
        draft.setTeam(team);
        assertTrue(repository.updateDraftPickWithTeam(draft));
        draftObjects = repository.findAllDraftPicksByYear(2024);
        assertEquals(3,draftObjects.get(1).getTeam().getTeamId());
    }


}