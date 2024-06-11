package gssato.wins_pool.data;

import gssato.wins_pool.models.DraftPick;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DraftPickRepository {
    List<DraftPick> findAllDraftPicks();

    DraftPick findDraftPickByNumber(int pickNumber);

    DraftPick addDraftPick(DraftPick draftPick);
    @Transactional
    boolean deleteByDraftPickId(int draftPickId);
}
