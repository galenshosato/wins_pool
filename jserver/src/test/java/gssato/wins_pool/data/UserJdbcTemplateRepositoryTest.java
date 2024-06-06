package gssato.wins_pool.data;

import gssato.wins_pool.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
        assertEquals(2, nonDeletedUsers.size());
    }

}