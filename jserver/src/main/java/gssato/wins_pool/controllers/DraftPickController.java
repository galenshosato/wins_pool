package gssato.wins_pool.controllers;

import gssato.wins_pool.domain.DraftPickService;
import gssato.wins_pool.domain.Result;
import gssato.wins_pool.models.DraftPick;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"})
@RequestMapping("/api/draftpick")
public class DraftPickController {

    @Autowired
    DraftPickService service;

    @GetMapping("/{draftPickNumber}")
    public DraftPick findByDraftNumber(@PathVariable int draftPickNumber) {return service.findDraftPickByNumber(draftPickNumber);}

    @PostMapping
    public ResponseEntity<Object> addDraftPick(@RequestBody DraftPick draftPick) {
        Result<DraftPick> result = service.addDraftPick(draftPick);

        if (result.isSuccess()) {
            return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
        }

        return ErrorResponse.build(result);
    }

    @DeleteMapping("/{draftPickId}")
    public ResponseEntity<Void> deleteByDraftPickId(@PathVariable int draftPickId) {
        if (service.deleteDraftPickById(draftPickId)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
