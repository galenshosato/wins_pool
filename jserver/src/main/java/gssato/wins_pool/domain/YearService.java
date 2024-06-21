package gssato.wins_pool.domain;

import gssato.wins_pool.data.YearRepository;
import gssato.wins_pool.models.Year;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class YearService {

    private final YearRepository repository;

    public YearService(YearRepository repository) {
        this.repository = repository;
    }

    public List<Year> findAllYears() {
        return repository.findAll();
    }

    public Year findByYear(int year) {
        return repository.findYearByYearNumber(year);
    }

    public Result<Year> addYear(Year year) {
        Result<Year> result = validate(year);

        if (!result.isSuccess()) {
            return result;
        }

        if (year.getYearId() != 0) {
            result.addMessage("yearId cannot be set for `add` operation", ResultType.INVALID);
            return result;
        }

        year = repository.addYear(year);
        result.setPayload(year);
        return result;
    }

    public Result<Year> updateYear(Year year) {
        Result<Year> result = validate(year);
        if (!result.isSuccess()) {
            return result;
        }

        if (year.getYearId() <=0) {
            result.addMessage("yearId must be set for the `update` operation", ResultType.INVALID);
            return result;
        }

        if (!repository.updateYear(year)) {
            String msg = String.format("yearId: %s is not found", year.getYearId());
            result.addMessage(msg, ResultType.NOT_FOUND);
        }

        return result;
    }

    private Result<Year> validate(Year year) {
        Result<Year> result = new Result<>();

        if (year == null) {
            result.addMessage("year object cannot be null", ResultType.INVALID);
            return result;
        }

        if (Validations.isNullOrBlank(Integer.toString(year.getYearNumber()))) {
            result.addMessage("year cannot be null", ResultType.INVALID);
        }

        if (year.getYearNumber() <= 0) {
            result.addMessage("You cannot have a negative year", ResultType.INVALID);
        }

        return result;
    }
}
