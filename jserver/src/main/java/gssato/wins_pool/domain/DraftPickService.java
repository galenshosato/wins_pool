package gssato.wins_pool.domain;

import gssato.wins_pool.data.DraftPickRepository;
import gssato.wins_pool.models.DraftPick;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DraftPickService {

    private final DraftPickRepository repository;

    public DraftPickService(DraftPickRepository repository) {
        this.repository = repository;
    }

    public DraftPick findDraftPickByNumber(int pickNumber) {
        return repository.findDraftPickByNumber(pickNumber);
    }

    public Result<DraftPick> addDraftPick(DraftPick draftPick) {
        Result<DraftPick> result = validate(draftPick);

        if (!result.isSuccess()) {
            return result;
        }

        if (draftPick.getDraftPickId() != 0) {
            result.addMessage("DraftPickId cannot be set for an `add` function", ResultType.INVALID);
            return result;
        }

        draftPick = repository.addDraftPick(draftPick);
        result.setPayload(draftPick);
        return result;
    }

    public boolean deleteDraftPickById(int draftPickId) {
        return repository.deleteByDraftPickId(draftPickId);
    }

    private Result<DraftPick> validate (DraftPick draftPick) {
        Result<DraftPick> result = new Result<>();
        List<DraftPick> currentPicks = repository.findAllDraftPicks();

        // check to see if picks have already been created
        if (currentPicks.size() >= 32) {
            result.addMessage("Draft Picks have already been created, please don't add more.", ResultType.INVALID);
            return result;
        }

        if (draftPick == null) {
            result.addMessage("Draft Pick object cannot be null", ResultType.INVALID);
            return result;
        }

        if (Validations.isNullOrBlank(Integer.toString(draftPick.getPickNumber()))) {
            result.addMessage("Draft Pick must be filled in", ResultType.INVALID);
        }

        if (draftPick.getPickNumber() <= 0 || draftPick.getPickNumber() > 40) {
            result.addMessage("Draft Pick Number must be between 1 and 40", ResultType.INVALID);
        }

        return result;
    }
}
