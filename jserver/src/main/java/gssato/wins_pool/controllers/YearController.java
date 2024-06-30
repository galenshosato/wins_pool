package gssato.wins_pool.controllers;

import gssato.wins_pool.domain.YearService;
import gssato.wins_pool.models.Year;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"})
@RequestMapping("/api/year")
public class YearController {

    @Autowired
    private YearService yearService;

    @GetMapping
    public List<Year> findAll() {return yearService.findAllYears();}

    @GetMapping("/{yearNumber}")
    public ResponseEntity<Year> findByYearNumber(@PathVariable int yearNumber) {
        Year year = yearService.findByYear(yearNumber);
        if (year == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(year);
    }



}
