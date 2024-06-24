package gssato.wins_pool.domain;

import gssato.wins_pool.data.DraftPickRepository;
import gssato.wins_pool.models.DraftPick;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class DraftPickServiceTest {

    @Autowired
    DraftPickService service;

    @MockBean
    DraftPickRepository repository;

    @Test
    void shouldNotAddDraftPickNull() {
        Result<DraftPick> result = service.addDraftPick(null);
        assertEquals(ResultType.INVALID, result.getType());
    }

    @Test
    void shouldNotAddDraftPickInvalidPickNumber() {
        DraftPick pick = makeDraftPick();
        pick.setPickNumber(-2);
        Result<DraftPick> result = service.addDraftPick(pick);
        assertEquals(ResultType.INVALID, result.getType());

        pick.setPickNumber(50);
        result = service.addDraftPick(pick);
        assertEquals(ResultType.INVALID, result.getType());
    }

    @Test
    void shouldAddDraftPick() {
        DraftPick pick = makeDraftPick();
        DraftPick mockPick = makeDraftPick();
        mockPick.setDraftPickId(1);

        when(repository.addDraftPick(pick)).thenReturn(mockPick);

        Result<DraftPick> result = service.addDraftPick(pick);
        assertEquals(ResultType.SUCCESS, result.getType());
    }

    @Test
    void shouldNotDeletePickIdNotFound() {
        when(repository.deleteByDraftPickId(4)).thenReturn(false);
        assertFalse(service.deleteDraftPickById(4));
    }

    @Test
    void shouldDeletePick() {
        when(repository.deleteByDraftPickId(1)).thenReturn(true);
        assertTrue(service.deleteDraftPickById(1));
    }

    DraftPick makeDraftPick() {
        DraftPick draftPick = new DraftPick();
        draftPick.setPickNumber(1);
        return draftPick;
    }
}