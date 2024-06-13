package gssato.wins_pool.data;

import gssato.wins_pool.models.Draft;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class DraftJdbcTemplateRepositoryTest {

    @Autowired
    DraftJdbcTemplateRepository repository;

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

}