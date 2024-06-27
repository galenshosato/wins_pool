package gssato.wins_pool.data;

import gssato.wins_pool.models.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserRepository {
    List<User> findAllUsers();

    List<User> findAllCurrentUsers();

    @Transactional
    User findUserById(int userId);

    @Transactional
    User findUserByEmail(String email);

    User addUser(User user);

    boolean updateUser(User user);

    boolean deleteUser(User user);
}
