package gssato.wins_pool.data;

import gssato.wins_pool.models.Year;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface YearRepository {
    List<Year> findAll();

    Year findYearByYearNumber(int yearNumber);

    Year addYear(Year year);

    boolean updateYear(Year year);
    @Transactional
    boolean deleteYearById(int yearId);
}
