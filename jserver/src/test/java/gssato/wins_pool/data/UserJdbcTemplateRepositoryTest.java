package gssato.wins_pool.data;

import gssato.wins_pool.models.Team;
import gssato.wins_pool.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserJdbcTemplateRepositoryTest {

    @Autowired
    UserJdbcTemplateRepository repository;

    @Autowired
    KnownGoodState knownGoodState;

    @BeforeEach
    void setup() {knownGoodState.set();}

    @Test
    void shouldFindAllUsers() {
        List<User> users = repository.findAllUsers();
        assertNotNull(users);
        assertTrue(users.size() >= 3);
    }

    @Test
    void shouldFindAllNotDeletedUsers() {
        List<User> nonDeletedUsers = repository.findAllCurrentUsers();
        assertNotNull(nonDeletedUsers);
        assertEquals(3, nonDeletedUsers.size());
    }

    @Test
    void shouldFindUserByEmail() {
        User user = repository.findUserByEmail("survivor@lightside.com");
        assertNotNull(user);
        assertEquals("Cal", user.getFirstName());
        assertEquals("Kestis", user.getLastName());
        assertEquals(2, user.getFavoriteTeam().getTeamId());
    }

    @Test
    void shouldNotFindUserByEmail() {
        User user = repository.findUserByEmail("mikeyMike@gmail.com");
        assertNull(user);
    }

    @Test
    void shouldAddUser() {
        User user = new User();
        Team favTeam = new Team();
        favTeam.setTeamId(2);
        user.setFirstName("Asohka");
        user.setLastName("Tano");
        user.setEmail("apprentice@gmail.com");
        user.setPassword("master?");
        user.setFavoriteTeam(favTeam);

        User result = repository.addUser(user);
        assertNotNull(result);

        assertEquals(4, result.getUserId());

        User asokha = repository.findUserByEmail("apprentice@gmail.com");
        assertNotNull(asokha);
        assertFalse(asokha.isDeleted());
        assertFalse(asokha.isAdmin());
        assertEquals(0, BigDecimal.valueOf(0.00).compareTo(asokha.getMoneyOwed()));
    }

}