package gssato.wins_pool.data;

import gssato.wins_pool.models.Year;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class YearJdbcTemplateRepositoryTest {

    @Autowired
    YearRepository repository;

    @Autowired
    KnownGoodState knownGoodState;

    @BeforeEach
    void setup() {knownGoodState.set();}

    @Test
    void shouldFindAllYears() {
        List<Year> years = repository.findAll();
        assertNotNull(years);
        assertTrue(years.size() >= 2);
    }

    @Test
    void shouldFindYearByYearNumber() {
        Year year = repository.findYearByYearNumber(2023);
        assertNotNull(year);
        assertEquals(1, year.getYearId());
    }

    @Test
    void shouldNotFindYearByNumber() {
        Year year = repository.findYearByYearNumber(2030);
        assertNull(year);
    }

    @Test
    void shouldAddYear() {
        Year newYear = new Year();
        newYear.setYearNumber(2026);
        Year actual = repository.addYear(newYear);
        assertNotNull(actual);
        assertEquals(4, actual.getYearId());
    }

    @Test
    void shouldUpdateYear() {
        Year newYear = new Year();
        newYear.setYearId(3);
        newYear.setYearNumber(2027);
        boolean result = repository.updateYear(newYear);
        assertTrue(result);
        Year actual = repository.findYearByYearNumber(2027);
        assertNotNull(actual);
        assertEquals(3, actual.getYearId());
    }

    @Test
    void shouldNotUpdateYear() {
        Year newYear = new Year();
        newYear.setYearId(6);
        newYear.setYearNumber(2028);
        assertFalse(repository.updateYear(newYear));
    }

    @Test
    void shouldDeleteYearById() {
        assertTrue(repository.deleteYearById(3));
    }

    @Test
    void shouldNotDeleteYearIdDoesNotExist() {
        assertFalse(repository.deleteYearById(10));
    }


}