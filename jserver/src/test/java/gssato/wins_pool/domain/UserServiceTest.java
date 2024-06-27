package gssato.wins_pool.domain;

import gssato.wins_pool.data.TeamRepository;
import gssato.wins_pool.data.UserRepository;
import gssato.wins_pool.dto.LoginDTO;
import gssato.wins_pool.dto.UserRequestDTO;
import gssato.wins_pool.models.Team;
import gssato.wins_pool.models.User;
import gssato.wins_pool.util.PasswordUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.awt.geom.RectangularShape;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserServiceTest {

    @Autowired
    UserService service;

    @MockBean
    TeamRepository teamRepository;

    @MockBean
    UserRepository repository;

    @Test
    void shouldNotLoginNullUser() {
        Result<User> result = service.login(null);
        assertEquals(ResultType.INVALID, result.getType());
    }

    @Test
    void shouldNotLoginNoUserFound() {
        LoginDTO logIn = new LoginDTO();

        logIn.setEmail("test@test.com");
        logIn.setPassword("password");

        Result<User> result = service.login(logIn);

        assertEquals(ResultType.NOT_FOUND, result.getType());
    }

    @Test
    void shouldNotLoginInvalidPassword() {
        User user = makeUser();
        user.setUserId(1);
        User mockUser = makeUser();
        mockUser.setUserId(1);
        String hashedPassword = PasswordUtils.hashPassword(user.getPassword());
        mockUser.setPassword(hashedPassword);

        when(repository.findUserByEmail(user.getEmail())).thenReturn(mockUser);

        LoginDTO login = new LoginDTO();
        login.setEmail("test@test.com");
        login.setPassword("wrong password");
        Result<User> result = service.login(login);

        assertEquals(ResultType.INVALID,result.getType());
    }

    @Test
    void shouldLogin() {
        User user = makeUser();
        user.setUserId(1);
        User mockUser = makeUser();
        mockUser.setUserId(1);
        String hashedPassword = PasswordUtils.hashPassword(user.getPassword());
        mockUser.setPassword(hashedPassword);

        when(repository.findUserByEmail(user.getEmail())).thenReturn(mockUser);

        LoginDTO logIn = new LoginDTO();
        logIn.setEmail("test@test.com");
        logIn.setPassword("test");

        Result<User> result = service.login(logIn);
        assertEquals(ResultType.SUCCESS,result.getType());
        assertNull(result.getPayload().getPassword());
    }

    @Test
    void shouldNotAddNullUser() {
        Result<User> result = service.addUser(null);
        assertEquals(ResultType.INVALID, result.getType());
    }

    @Test
    void shouldNotAddNoFirstName() {
        UserRequestDTO request = makeRequestDTO();
        request.setFirstName("");
        Result<User> result = service.addUser(request);
        assertEquals(ResultType.INVALID, result.getType());
    }

    @Test
    void shouldNotAddNoLastName() {
        UserRequestDTO request = makeRequestDTO();
        request.setLastName(null);
        Result<User> result = service.addUser(request);
        assertEquals(ResultType.INVALID, result.getType());
    }

    @Test
    void shouldNotAddNoEmail() {
        UserRequestDTO request = makeRequestDTO();
        request.setEmail(null);
        Result<User> result = service.addUser(request);
        assertEquals(ResultType.INVALID, result.getType());
    }

    @Test
    void shouldNotAddNoPassword() {
        UserRequestDTO request = makeRequestDTO();
        request.setPassword(null);
        Result<User> result = service.addUser(request);
        assertEquals(ResultType.INVALID, result.getType());
    }

    @Test
    void shouldNotAddNoFavoriteTeam() {
        UserRequestDTO request = makeRequestDTO();
        request.setFavoriteTeamId(0);
        Result<User> result = service.addUser(request);
        assertEquals(ResultType.INVALID, result.getType());
    }

    @Test
    void shouldAddNewUser() {
        User mockUser = makeUser();
        UserRequestDTO request = makeRequestDTO();
        String hashedPassword = PasswordUtils.hashPassword(request.getPassword());
        mockUser.setUserId(1);
        mockUser.setPassword(hashedPassword);


        when(repository.addUser(any(User.class))).thenReturn(mockUser);

        Result<User> result = service.addUser(request);
        assertEquals(ResultType.SUCCESS, result.getType());
        mockUser.setPassword(null);
        assertEquals(mockUser, result.getPayload());
        assertNull(result.getPayload().getPassword());
    }

    @Test
    void shouldNotUpdateUserInvalidUser() {
        Result<User> result = service.updateUser(null);
        assertEquals(ResultType.INVALID, result.getType());

        UserRequestDTO request = makeRequestDTO();
        request.setFirstName(" ");
        result = service.updateUser(request);
        assertEquals(ResultType.INVALID, result.getType());

        request = makeRequestDTO();
        request.setLastName(null);
        result = service.updateUser(request);
        assertEquals(ResultType.INVALID, result.getType());

        request = makeRequestDTO();
        request.setEmail("");
        result = service.updateUser(request);
        assertEquals(ResultType.INVALID, result.getType());

        request = makeRequestDTO();
        request.setPassword(null);
        result = service.updateUser(request);
        assertEquals(ResultType.INVALID, result.getType());

        request = makeRequestDTO();
        request.setFavoriteTeamId(0);
        result = service.updateUser(request);
        assertEquals(ResultType.INVALID, result.getType());

        request = makeRequestDTO();
        request.setUserId(0);
        result = service.updateUser(request);
        assertEquals(ResultType.INVALID, result.getType());
    }

    @Test
    void shouldNotUpdateIdNotFound() {
        User user = makeUser();
        user.setUserId(50);

        UserRequestDTO request = makeRequestDTO();
        request.setUserId(50);

        when(repository.updateUser(user)).thenReturn(false);

        Result<User> result = service.updateUser(request);
        assertEquals(ResultType.NOT_FOUND, result.getType());
    }

    @Test
    void shouldUpdateUser() {
        User user = makeUser();
        user.setUserId(1);
        User mockUser = makeUser();
        mockUser.setUserId(1);
        when(repository.findUserById(1)).thenReturn(mockUser);

        UserRequestDTO request = makeRequestDTO();
        request.setUserId(1);

        when(repository.updateUser(mockUser)).thenReturn(true);

        Result<User> result = service.updateUser(request);
        assertEquals(ResultType.SUCCESS, result.getType());
    }

    @Test
    void shouldNotUpdateMoneyIdNotFound() {
        Result<User> result = service.updateUserMoney(50, BigDecimal.valueOf(5.00));
        assertEquals(ResultType.NOT_FOUND, result.getType());
    }

    @Test
    void shouldNotUpdateWhenUpdateFails() {
        int userId = 1;
        BigDecimal addedMoney = BigDecimal.valueOf(100);
        User user = new User();
        user.setUserId(userId);
        user.setMoneyOwed(BigDecimal.valueOf(200));

        when(repository.findUserById(userId)).thenReturn(user);
        when(repository.updateUser(any(User.class))).thenReturn(false);

        Result<User> result = service.updateUserMoney(userId, addedMoney);

        assertEquals(ResultType.INVALID, result.getType());

    }

    @Test
    void shouldReturnSuccessWhenUpdateSucceeds() {
        int userId = 1;
        BigDecimal addedMoney = BigDecimal.valueOf(100);
        User user = new User();
        user.setUserId(userId);
        user.setMoneyOwed(BigDecimal.valueOf(200));

        when(repository.findUserById(userId)).thenReturn(user);
        when(repository.updateUser(any(User.class))).thenReturn(true);

        Result<User> result = service.updateUserMoney(userId, addedMoney);

        assertEquals(ResultType.SUCCESS, result.getType());
        assertNotNull(result.getPayload());
        assertEquals(BigDecimal.valueOf(300), result.getPayload().getMoneyOwed());
    }

    @Test
    void shouldDeleteUser() {
        User user = makeUser();
        user.setUserId(1);

        when(repository.deleteUser(user)).thenReturn(true);

        assertTrue(service.deleteUser(user));
    }

    @Test
    void shouldNotDeleteUser() {
        User user = makeUser();
        user.setUserId(100);

        when(repository.deleteUser(user)).thenReturn(false);

        assertFalse(service.deleteUser(user));
    }

    User makeUser() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("test");
        user.setFirstName("Testy");
        user.setLastName("McTesterson");
        user.setDeleted(false);
        user.setAdmin(false);
        user.setMoneyOwed(BigDecimal.ZERO);

        Team team = new Team();
        team.setTeamId(2);
        team.setLocation("New England");
        team.setTeamName("Patriots");
        team.setColor("blue");
        team.setAltColor("white");
        user.setFavoriteTeam(team);

        return user;
    }

    UserRequestDTO makeRequestDTO() {
        UserRequestDTO request = new UserRequestDTO();
        request.setFirstName("Testy");
        request.setLastName("McTesterson");
        request.setEmail("test@test.com");
        request.setPassword("test");
        request.setFavoriteTeamId(2);
        return request;
    }

}