package gssato.wins_pool.domain;

import gssato.wins_pool.data.DraftRepository;
import gssato.wins_pool.dto.DraftRequestDTO;
import gssato.wins_pool.models.Draft;
import gssato.wins_pool.models.DraftPick;
import gssato.wins_pool.models.User;
import gssato.wins_pool.models.Year;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DraftService {

    private final DraftRepository repository;
    private final UserService userService;
    private final YearService yearService;
    private final TeamService teamService;
    private final DraftPickService draftPickService;

    @Autowired
    public DraftService(DraftRepository repository, UserService userService, YearService yearService, TeamService teamService, DraftPickService draftPickService) {
        this.repository = repository;
        this.userService = userService;
        this.yearService = yearService;
        this.teamService = teamService;
        this.draftPickService = draftPickService;
    }


    public List<Draft> findAllDraftPicksByYear(int year) {
        return repository.findAllDraftPicksByYear(year);
    }

    public List<Draft> findAllDraftPicksByUserIdAndYear(int year, int userId) {
        return repository.findAllDraftPicksByUserAndYear(year, userId);
    }

    public Result<Draft> createDraftSelection(DraftRequestDTO draftRequestDto) {
        Result<Draft> result = validate(draftRequestDto);

        if (!result.isSuccess()) {
            return result;
        }

        if (draftRequestDto.getDraftId() != 0) {
            result.addMessage("Draft Object must not have an ID for a `create` operation", ResultType.INVALID);
            return result;
        }

        if (draftRequestDto.getTeamId() != 0) {
            result.addMessage("Draft Object cannot have a team associated with it upon creation", ResultType.INVALID);
            return result;
        }

        Draft draft = new Draft();
        User user = userService.findUserByEmail(draftRequestDto.getUserEmail());
        Year year = yearService.findByYear(draftRequestDto.getYear());
        DraftPick pickNumber = draftPickService.findDraftPickByNumber(draftRequestDto.getPickNumber());
        draft.setUser(user);
        draft.setYear(year);
        draft.setDraftPick(pickNumber);

        draft = repository.createDraftPick(draft);
        result.setPayload(draft);
        return result;
    }



    //TODO: Create Update/Add Team method



    private Result<Draft> validate (DraftRequestDTO draftRequestDto) {
        Result<Draft> result = new Result<>();

        if (draftRequestDto == null) {
            result.addMessage("Draft Object cannot be null", ResultType.INVALID);
            return result;
        }

        if (Validations.isNullOrBlank(draftRequestDto.getUserEmail())) {
            result.addMessage("A User must be assigned to a Draft Pick", ResultType.INVALID);
        }

        if (draftRequestDto.getYear() < 2020) {
            result.addMessage("Please enter a valid year", ResultType.INVALID);
        }

        if (draftRequestDto.getPickNumber() == 0) {
            result.addMessage("A Draft Pick Number must be assigned to a Draft Pick", ResultType.INVALID);
        }

        return result;
    }
}
