package gssato.wins_pool.data;


import gssato.wins_pool.models.DraftPick;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class DraftPickJdbcTemplateRepositoryTest {

    @Autowired
    DraftPickRepository repository;

    @Autowired
    KnownGoodState knownGoodState;

    @BeforeEach
    void setup() {knownGoodState.set();}

    @Test
    void shouldFindAllDraftPicks() {
        List<DraftPick> picks = repository.findAllDraftPicks();
        assertNotNull(picks);
        assertTrue(picks.size() >= 5);

    }

    @Test
    void shouldFindByPickNumber() {
        DraftPick pick4 = repository.findDraftPickByNumber(4);
        assertNotNull(pick4);
        assertEquals(4, pick4.getDraftPickId());

        DraftPick pick2 = repository.findDraftPickByNumber(2);
        assertNotNull(pick2);
    }

    @Test
    void shouldNotFindByPickNumber() {
        DraftPick pick100 = repository.findDraftPickByNumber(100);
        assertNull(pick100);
    }

    @Test
    void shouldAddDraftPick() {
        DraftPick draftPick = new DraftPick();
        draftPick.setPickNumber(6);
        DraftPick actual = repository.addDraftPick(draftPick);
        assertNotNull(actual);
        assertEquals(6, actual.getPickNumber());
    }

    @Test
    void shouldNotDeleteDraftPick() {
        assertFalse(repository.deleteByDraftPickId(100));
    }

    @Test
    void shouldDeleteDraftPick() {
        DraftPick pick7 = new DraftPick();
        pick7.setPickNumber(7);
        DraftPick actual = repository.addDraftPick(pick7);
        assertNotNull(actual);

        assertTrue(repository.deleteByDraftPickId(actual.getDraftPickId()));
        assertNull(repository.findDraftPickByNumber(7));
    }

}