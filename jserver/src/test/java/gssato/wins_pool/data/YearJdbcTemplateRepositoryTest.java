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
    YearJdbcTemplateRepository repository;

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

}