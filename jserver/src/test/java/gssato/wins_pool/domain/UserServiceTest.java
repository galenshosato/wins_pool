package gssato.wins_pool.domain;

import gssato.wins_pool.data.UserRepository;
import gssato.wins_pool.models.Team;
import gssato.wins_pool.models.User;
import gssato.wins_pool.util.PasswordUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserServiceTest {

    @Autowired
    UserService service;

    @MockBean
    UserRepository repository;

    @Test
    void shouldNotLoginNullUser() {
        Result<User> result = service.login(null);
        assertEquals(ResultType.INVALID, result.getType());
    }

    @Test
    void shouldNotLoginNoUserFound() {
        User user = makeUser();

        Result<User> result = service.login(user);

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
        user.setPassword("wrongPassword");
        Result<User> result = service.login(user);

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

        Result<User> result = service.login(user);
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
        User user = makeUser();
        user.setFirstName("");
        Result<User> result = service.addUser(user);
        assertEquals(ResultType.INVALID, result.getType());
    }

    @Test
    void shouldNotAddNoLastName() {
        User user = makeUser();
        user.setLastName(null);
        Result<User> result = service.addUser(user);
        assertEquals(ResultType.INVALID, result.getType());
    }

    @Test
    void shouldNotAddNoEmail() {
        User user = makeUser();
        user.setEmail(null);
        Result<User> result = service.addUser(user);
        assertEquals(ResultType.INVALID, result.getType());
    }

    @Test
    void shouldNotAddNoPassword() {
        User user = makeUser();
        user.setPassword(null);
        Result<User> result = service.addUser(user);
        assertEquals(ResultType.INVALID, result.getType());
    }

    @Test
    void shouldNotAddNoFavoriteTeam() {
        User user = makeUser();
        user.setFavoriteTeam(null);
        Result<User> result = service.addUser(user);
        assertEquals(ResultType.INVALID, result.getType());
    }

    @Test
    void shouldAddNewUser() {
        User user = makeUser();
        User mockUser = makeUser();
        String hashedPassword = PasswordUtils.hashPassword(user.getPassword());
        mockUser.setUserId(1);
        mockUser.setPassword(hashedPassword);

        when(repository.addUser(user)).thenReturn(mockUser);
        Result<User> result = service.addUser(user);
        assertEquals(ResultType.SUCCESS, result.getType());
        mockUser.setPassword(null);
        assertEquals(mockUser, result.getPayload());
        assertNull(result.getPayload().getPassword());
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
        team.setLocation("New England");
        team.setTeamName("Patriots");
        team.setColor("Red");
        team.setAltColor("White");
        user.setFavoriteTeam(team);

        return user;
    }

}