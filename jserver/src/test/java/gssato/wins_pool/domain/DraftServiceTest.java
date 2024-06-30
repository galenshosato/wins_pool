package gssato.wins_pool.domain;

import gssato.wins_pool.data.DraftRepository;
import gssato.wins_pool.dto.DraftRequestDTO;
import gssato.wins_pool.models.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class DraftServiceTest {

    @Autowired
    DraftService draftService;

    @MockBean
    TeamService teamService;

    @MockBean
    UserService userService;

    @MockBean
    YearService yearService;

    @MockBean
    DraftPickService draftPickService;

    @MockBean
    DraftRepository repository;

    @Test
    void shouldNotAddNullDraftObject() {
        Result<Draft> result = draftService.createDraftSelection(null);
        assertEquals(ResultType.INVALID, result.getType());
    }

    @Test
    void shouldNotAddNoUser() {
        DraftRequestDTO request = makeRequestDTO();
        request.setUserEmail("");
        Result<Draft> result = draftService.createDraftSelection(request);
        assertEquals(ResultType.INVALID, result.getType());
    }

    @Test
    void shouldNotAddInvalidYear() {
        DraftRequestDTO request = makeRequestDTO();
        request.setYear(2019);
        Result<Draft> result = draftService.createDraftSelection(request);
        assertEquals(ResultType.INVALID, result.getType());

    }

    @Test
    void shouldNotAddNoPickNumberAssigned() {
        DraftRequestDTO request = makeRequestDTO();
        request.setPickNumber(0);
        Result<Draft> result = draftService.createDraftSelection(request);
        assertEquals(ResultType.INVALID, result.getType());
    }

    @Test
    void shouldNotAddInvalidDraftId() {
        DraftRequestDTO request = makeRequestDTO();
        request.setDraftId(1);

        Result<Draft> result = draftService.createDraftSelection(request);

        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getType());
        assertEquals("Draft Object must not have an ID for a `create` operation", result.getMessages().get(0));
    }

    @Test
    void shouldNotAddInvalidTeamId() {
        DraftRequestDTO request = makeRequestDTO();
        request.setTeamId(2);
        Result<Draft> result = draftService.createDraftSelection(request);
        assertEquals(ResultType.INVALID,result.getType());
        assertFalse(result.isSuccess());
        assertEquals("Draft Object cannot have a team associated with it upon creation", result.getMessages().get(0));
    }

    @Test
    void shouldCreateDraftSelectionObject() {
        DraftRequestDTO request = makeRequestDTO();

        User user = new User();
        Year year = new Year();
        DraftPick pickNumber = new DraftPick();
        Draft draft = new Draft();

        when(userService.findUserByEmail("test@test.com")).thenReturn(user);
        when(yearService.findByYear(2023)).thenReturn(year);
        when(draftPickService.findDraftPickByNumber(1)).thenReturn(pickNumber);
        when(repository.createDraftPick(any(Draft.class))).thenReturn(draft);

        Result<Draft> result = draftService.createDraftSelection(request);

        assertTrue(result.isSuccess());
        assertEquals(draft, result.getPayload());
        assertEquals(ResultType.SUCCESS, result.getType());
    }

    @Test
    void shouldNotUpdateDraftObjectInvalidDraftId() {
        Result<Draft> result = draftService.updateDraftWithTeam(10,2);
        assertEquals(ResultType.NOT_FOUND, result.getType());
        assertEquals("Draft Object was not found, make sure Id is correct", result.getMessages().get(0));
    }

    @Test
    void shouldNotUpdateDraftObjectTeamAlreadyAssigned() {
        Draft draft = new Draft();
        draft.setDraftId(1);
        Team team = new Team();
        team.setTeamId(1);
        draft.setTeam(team);

        when(repository.findDraftObjectById(1)).thenReturn(draft);

        Result<Draft> result = draftService.updateDraftWithTeam(1, 3);
        assertEquals(ResultType.INVALID, result.getType());
        assertEquals("This draft object already has a team drafted", result.getMessages().get(0));
    }

    @Test
    void shouldNotUpdateDraftObjectInvalidTeamId() {
        Draft draft = new Draft();
        draft.setDraftId(1);

        when(repository.findDraftObjectById(1)).thenReturn(draft);
        when(teamService.findTeamById(2)).thenReturn(null);

        Result<Draft> result = draftService.updateDraftWithTeam(1,2);
        assertEquals(ResultType.INVALID,result.getType());
        assertEquals("Invalid Team Id",result.getMessages().get(0));
    }

    @Test
    void shouldUpdateDraftObject() {
        Draft draft = new Draft();
        draft.setDraftId(1);
        Team team = new Team();
        team.setTeamId(1);

        when(repository.findDraftObjectById(1)).thenReturn(draft);
        when(teamService.findTeamById(1)).thenReturn(team);
        when(repository.updateDraftPickWithTeam(draft)).thenReturn(true);

        Result<Draft> result = draftService.updateDraftWithTeam(1,1);

        assertEquals(ResultType.SUCCESS, result.getType());

    }


    DraftRequestDTO makeRequestDTO() {
        DraftRequestDTO requestDTO = new DraftRequestDTO();
        requestDTO.setUserEmail("test@test.com");
        requestDTO.setYear(2023);
        requestDTO.setPickNumber(1);

        return requestDTO;
    }

}