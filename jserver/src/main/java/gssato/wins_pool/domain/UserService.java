package gssato.wins_pool.domain;

import gssato.wins_pool.data.UserRepository;
import gssato.wins_pool.dto.LoginDTO;
import gssato.wins_pool.dto.UserRequestDTO;
import gssato.wins_pool.models.Team;
import gssato.wins_pool.models.User;
import gssato.wins_pool.util.PasswordUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;
    private final TeamService teamService;

    public UserService(UserRepository repository, TeamService teamService) {
        this.repository = repository;
        this.teamService = teamService;
    }

    public List<User> findAllUsers() {return repository.findAllUsers();}

    public List<User> findAllCurrentUsers() {return repository.findAllCurrentUsers();}

    public User findUserByEmail(String email) {
        User user = repository.findUserByEmail(email);
        user.setPassword(null);
        return user;
    }

    public Result<User> login(LoginDTO loginDTO) {
        return validateLogin(loginDTO);
    }

    public Result<User> addUser(UserRequestDTO userRequestDTO) {
        Result<User> result = validateUserDTO(userRequestDTO);
        if (!result.isSuccess()) {
            return result;
        }

        if (userRequestDTO.getUserId() != 0) {
            result.addMessage("User Id cannot be set for an `add` operation", ResultType.INVALID);
            return result;
        }

        User user = new User();
        user.setFirstName(userRequestDTO.getFirstName());
        user.setLastName(userRequestDTO.getLastName());
        user.setEmail(userRequestDTO.getEmail());

        Team favoriteTeam = teamService.findTeamById(userRequestDTO.getFavoriteTeamId());
        user.setFavoriteTeam(favoriteTeam);

        String hashedPassword = PasswordUtils.hashPassword(userRequestDTO.getPassword());
        user.setPassword(hashedPassword);
        User Addeduser = repository.addUser(user);
        Addeduser.setPassword(null);
        result.setPayload(Addeduser);
        return result;
    }

    public Result<User> updateUser(UserRequestDTO userRequestDTO) {
        Result<User> result = validateUserDTO(userRequestDTO);

        if (!result.isSuccess()) {
            return result;
        }

        if (userRequestDTO.getUserId() <= 0) {
            result.addMessage("userId must be set for an `update` operation", ResultType.INVALID);
            return result;
        }

        User user = repository.findUserById(userRequestDTO.getUserId());
        if (user == null) {
            String msg = String.format("User Id: %s was not found", userRequestDTO.getUserId());
            result.addMessage(msg,ResultType.NOT_FOUND);
            return result;
        }

        user.setFirstName(userRequestDTO.getFirstName());
        user.setLastName(userRequestDTO.getLastName());
        user.setEmail(userRequestDTO.getEmail());
        String hashedPassword = PasswordUtils.hashPassword(userRequestDTO.getPassword());
        if (!hashedPassword.equals(user.getPassword())) {
            user.setPassword(hashedPassword);
        }

        if (!repository.updateUser(user)) {
            result.addMessage("There was an error with your update, please try again", ResultType.INVALID);
        }

        return result;
    }

    public Result<User> updateUserMoney(int userId, BigDecimal addedMoney) {
        Result<User> result = new Result<>();
        User user = repository.findUserById(userId);
        if (user == null) {
            String msg = String.format("User Id: %s was not found", userId);
            result.addMessage(msg,ResultType.NOT_FOUND);
            return result;
        }

        BigDecimal newMoneyOwed = user.getMoneyOwed().add(addedMoney);
        user.setMoneyOwed(newMoneyOwed);
        if (!repository.updateUser(user)) {
            result.addMessage("There was an error with your update, please try again", ResultType.INVALID);
        }

        result.setPayload(user);

        return result;
    }

    public boolean deleteUser(User user) {
        return repository.deleteUser(user);
    }


    private Result<User> validateLogin(LoginDTO loginDTO) {
        Result<User> result = new Result<>();

        if (loginDTO == null) {
            result.addMessage("Login Credentials cannot be null", ResultType.INVALID);
            return result;
        }

        User checkedUser = repository.findUserByEmail(loginDTO.getEmail());

        if (checkedUser == null) {
            result.addMessage("User not found", ResultType.NOT_FOUND);
            return result;
        }

        boolean verified = PasswordUtils.verifyPassword(loginDTO.getPassword(), checkedUser.getPassword());

        if (!verified) {
            result.addMessage("Invalid password for this user", ResultType.INVALID);
            return result;
        }

        checkedUser.setPassword(null);
        result.setPayload(checkedUser);
        return result;

    }

    private Result<User> validateUserDTO(UserRequestDTO userRequestDTO) {
        Result<User> result = new Result<>();

        if (userRequestDTO == null) {
            result.addMessage("New User cannot be null", ResultType.INVALID);
            return result;
        }

        if (Validations.isNullOrBlank(userRequestDTO.getFirstName())) {
            result.addMessage("User must have a first name", ResultType.INVALID);
        }

        if (Validations.isNullOrBlank(userRequestDTO.getLastName())) {
            result.addMessage("User must have a last name", ResultType.INVALID);
        }

        if (Validations.isNullOrBlank(userRequestDTO.getEmail())) {
            result.addMessage("User must have an email", ResultType.INVALID);
        }

        if (Validations.isNullOrBlank(userRequestDTO.getPassword())) {
            result.addMessage("User must have a password", ResultType.INVALID);
        }

        if (userRequestDTO.getFavoriteTeamId() == 0) {
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
