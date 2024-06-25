package gssato.wins_pool.domain;

import gssato.wins_pool.data.DraftRepository;
import gssato.wins_pool.dto.DraftDTO;
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

    public Result<Draft> createDraftSelection(DraftDTO draftDto) {
        Result<Draft> result = validate(draftDto);

        if (!result.isSuccess()) {
            return result;
        }

        if (draftDto.getDraftId() != 0) {
            result.addMessage("Draft Object must not have an ID for a `create` operation", ResultType.INVALID);
            return result;
        }

        if (draftDto.getTeamId() != 0) {
            result.addMessage("Draft Object cannot have a team associated with it upon creation", ResultType.INVALID);
            return result;
        }

        Draft draft = new Draft();
        User user = userService.findUserByEmail(draftDto.getUserEmail());
        Year year = yearService.findByYear(draftDto.getYear());
        DraftPick pickNumber = draftPickService.findDraftPickByNumber(draftDto.getPickNumber());
        draft.setUser(user);
        draft.setYear(year);
        draft.setDraftPick(pickNumber);

        draft = repository.createDraftPick(draft);
        result.setPayload(draft);
        return result;
    }

    //TODO: Do I need to pass draft objects back or can it just be a boolean? And if so, I need to go back and edit the repository as well

    //TODO: Create Update/Add Team method

    private Result<Draft> validate (DraftDTO draftDto) {
        Result<Draft> result = new Result<>();

        if (draftDto == null) {
            result.addMessage("Draft Object cannot be null", ResultType.INVALID);
            return result;
        }

        if (Validations.isNullOrBlank(draftDto.getUserEmail())) {
            result.addMessage("A User must be assigned to a Draft Pick", ResultType.INVALID);
        }

        if (draftDto.getYear() < 2020) {
            result.addMessage("Please enter a valid year", ResultType.INVALID);
        }

        if (draftDto.getPickNumber() == 0) {
            result.addMessage("A Draft Pick Number must be assigned to a Draft Pick", ResultType.INVALID);
        }

        return result;
    }
}
