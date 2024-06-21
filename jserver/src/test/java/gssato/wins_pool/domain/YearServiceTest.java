package gssato.wins_pool.domain;


import gssato.wins_pool.data.YearRepository;
import gssato.wins_pool.models.Year;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class YearServiceTest {
    @Autowired
    YearService service;

    @MockBean
    YearRepository repository;

    @Test
    void shouldNotAddNullYear() {
        Result<Year> actual = service.addYear(null);
        assertEquals(ResultType.INVALID, actual.getType());
    }

    @Test
    void shouldNotAddYearIdAlreadySet() {
        Year year = makeYear();
        year.setYearId(1);
        Result<Year> actual = service.addYear(year);
        assertEquals(ResultType.INVALID, actual.getType());
    }

    @Test
    void shouldNotAddYearInvalidYear() {
        Year year = makeYear();
        year.setYearNumber(0);
        Result<Year> actual = service.addYear(year);
        assertEquals(ResultType.INVALID, actual.getType());
    }

    @Test
    void shouldAddYear() {
        Year year = makeYear();
        Year mockYear = makeYear();
        mockYear.setYearId(1);

        when(repository.addYear(year)).thenReturn(mockYear);
        Result<Year> actual = service.addYear(year);
        assertEquals(ResultType.SUCCESS, actual.getType());
        assertEquals(mockYear, actual.getPayload());
    }

    @Test
    void shouldNotUpdateInvalidId() {
        Year year = makeYear();
        year.setYearId(-5);
        Result<Year> actual = service.updateYear(year);
        assertEquals(ResultType.INVALID, actual.getType());
    }

    @Test
    void shouldNotUpdateNullObject() {
        Result<Year> actual = service.updateYear(null);
        assertEquals(ResultType.INVALID, actual.getType());
    }

    @Test
    void shouldNotUpdateIdNotFound() {
        Year year = makeYear();
        year.setYearId(10);
        when(repository.updateYear(year)).thenReturn(false);
        Result<Year> actual = service.updateYear(year);
        assertEquals(ResultType.NOT_FOUND, actual.getType());
    }

    @Test
    void shouldUpdate() {
        Year year = makeYear();
        year.setYearId(1);
        when(repository.updateYear(year)).thenReturn(true);
        Result<Year> actual = service.updateYear(year);
        assertEquals(ResultType.SUCCESS, actual.getType());
    }

    private Year makeYear() {
        Year year = new Year();
        year.setYearNumber(2024);
        return year;
    }
}