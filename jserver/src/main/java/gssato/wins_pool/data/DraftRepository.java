package gssato.wins_pool.data;

import gssato.wins_pool.models.Draft;

import java.util.List;

public interface DraftRepository {
    List<Draft> findAllDraftPicksByYear(int year);

    List<Draft> findAllDraftPicksByUserAndYear(int year, int userId);

    Draft findDraftObjectById(int draftId);

    Draft createDraftPick(Draft draftPick);

    boolean updateDraftPickWithTeam(Draft draftPick);
}
