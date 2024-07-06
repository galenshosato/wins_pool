package gssato.wins_pool.controllers;

import gssato.wins_pool.domain.DraftService;
import gssato.wins_pool.domain.Result;
import gssato.wins_pool.dto.DraftRequestDTO;
import gssato.wins_pool.models.Draft;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"})
@RequestMapping("/api/draft")
public class DraftController {

    @Autowired
    DraftService service;

    @GetMapping("/{year}")
    public List<Draft> findAllDraftObjectsByYear(@PathVariable int year) {
        return service.findAllDraftPicksByYear(year);
    }

    @GetMapping("/{year}/{userId}")
    public List<Draft> findAllDraftObjectsForUserByYear(@PathVariable int year, @PathVariable int userId) {
        return service.findAllDraftPicksByUserIdAndYear(year, userId);
    }

    @PostMapping
    public ResponseEntity<Object> createNewDraftObject(@RequestBody DraftRequestDTO request) {
        Result<Draft> result = service.createDraftSelection(request);
        if (result.isSuccess()) {
            return new ResponseEntity<>(result.getPayload(), HttpStatus.OK);
        }

        return ErrorResponse.build(result);
    }

    @PatchMapping("/{draftId}")
    public ResponseEntity<Object> updateDraftObjectWithTeam(@PathVariable int draftId, @RequestBody int teamId) {
        Result<Draft> result = service.updateDraftWithTeam(draftId, teamId);

        if (result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return ErrorResponse.build(result);
    }


}
