package gssato.wins_pool.domain;

import gssato.wins_pool.data.UserRepository;
import gssato.wins_pool.models.User;
import gssato.wins_pool.util.PasswordUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public List<User> findAllUsers() {return repository.findAllUsers();}

    public List<User> findAllCurrentUsers() {return repository.findAllCurrentUsers();}

    public User findUserByEmail(String email) {
        User user = repository.findUserByEmail(email);
        user.setPassword(null);
        return user;
    }

    public Result<User> login(User user) {
        return validateLogin(user);
    }

    public Result<User> addUser(User user) {
        Result<User> result = validateNewUser(user);
        if (!result.isSuccess()) {
            return result;
        }

        if (user.getUserId() != 0) {
            result.addMessage("UserId cannot be set for `add` operation", ResultType.INVALID);
            return result;
        }

        String hashedPassword = PasswordUtils.hashPassword(user.getPassword());
        user.setPassword(hashedPassword);
        user = repository.addUser(user);
        user.setPassword(null);
        result.setPayload(user);
        return result;
    }


    private Result<User> validateLogin(User user) {
        Result<User> result = new Result<>();

        checkNullUser(user, result);

        if (!result.isSuccess()) {
            return result;
        }

        User checkedUser = repository.findUserByEmail(user.getEmail());

        if (checkedUser == null) {
            result.addMessage("User not found", ResultType.NOT_FOUND);
            return result;
        }

        boolean verified = PasswordUtils.verifyPassword(user.getPassword(), checkedUser.getPassword());

        if (!verified) {
            result.addMessage("Invalid password for this user", ResultType.INVALID);
            return result;
        }

        checkedUser.setPassword(null);
        result.setPayload(checkedUser);
        return result;

    }

    private Result<User> validateNewUser(User user) {
        Result<User> result = new Result<>();

        checkNullUser(user, result);

        if (!result.isSuccess()) {
            return result;
        }

        if (Validations.isNullOrBlank(user.getFirstName())) {
            result.addMessage("User must have a first name", ResultType.INVALID);
        }

        if (Validations.isNullOrBlank(user.getLastName())) {
            result.addMessage("User must have a last name", ResultType.INVALID);
        }

        if (Validations.isNullOrBlank(user.getEmail())) {
            result.addMessage("User must have an email", ResultType.INVALID);
        }

        if (Validations.isNullOrBlank(user.getPassword())) {
            result.addMessage("User must have a password", ResultType.INVALID);
        }

        if (user.getFavoriteTeam() == null) {
            result.addMessage("User must have a favorite team", ResultType.INVALID);
        }

        return result;
    }

    private void checkNullUser(User user, Result<User> result) {
        if (user == null) {
            result.addMessage("User object cannot be null", ResultType.INVALID);
        }
    }
}
