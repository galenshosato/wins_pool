package gssato.wins_pool.domain;

import gssato.wins_pool.data.UserRepository;
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

    @Test
    void shouldNotUpdateUserInvalidUser() {
        Result<User> result = service.updateUser(null);
        assertEquals(ResultType.INVALID, result.getType());

        User user = makeUser();
        user.setFirstName(" ");
        result = service.updateUser(user);
        assertEquals(ResultType.INVALID, result.getType());

        user = makeUser();
        user.setLastName(null);
        result = service.updateUser(user);
        assertEquals(ResultType.INVALID, result.getType());

        user = makeUser();
        user.setEmail("");
        result = service.updateUser(user);
        assertEquals(ResultType.INVALID, result.getType());

        user = makeUser();
        user.setPassword(null);
        result = service.updateUser(user);
        assertEquals(ResultType.INVALID, result.getType());

        user = makeUser();
        user.setFavoriteTeam(null);
        result = service.updateUser(user);
        assertEquals(ResultType.INVALID, result.getType());

        user = makeUser();
        user.setUserId(0);
        result = service.updateUser(user);
        assertEquals(ResultType.INVALID, result.getType());
    }

    @Test
    void shouldNotUpdateIdNotFound() {
        User user = makeUser();
        user.setUserId(50);

        when(repository.updateUser(user)).thenReturn(false);

        Result<User> result = service.updateUser(user);
        assertEquals(ResultType.NOT_FOUND, result.getType());
    }

    @Test
    void shouldUpdateUser() {
        User user = makeUser();
        user.setUserId(1);

        when(repository.updateUser(user)).thenReturn(true);

        Result<User> result = service.updateUser(user);
        assertEquals(ResultType.SUCCESS, result.getType());
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
        team.setLocation("New England");
        team.setTeamName("Patriots");
        team.setColor("Red");
        team.setAltColor("White");
        user.setFavoriteTeam(team);

        return user;
    }

}