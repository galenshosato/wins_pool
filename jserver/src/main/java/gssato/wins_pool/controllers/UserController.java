package gssato.wins_pool.controllers;

import gssato.wins_pool.domain.Result;
import gssato.wins_pool.domain.UserService;
import gssato.wins_pool.dto.LoginDTO;
import gssato.wins_pool.dto.UserRequestDTO;
import gssato.wins_pool.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"})
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserService service;

    @GetMapping("/all")
    public List<User> findAllUsers() {return service.findAllUsers();}

    @GetMapping
    public ResponseEntity<Object> findUserByEmail(@RequestParam String email) {
        User user = service.findUserByEmail(email);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/current")
    public List<User> findAllCurrentUsers() {return service.findAllCurrentUsers();}

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginDTO loginDTO) {
        Result<User> result = service.login(loginDTO);

        if (result.isSuccess()) {
            return new ResponseEntity<>(result.getPayload(), HttpStatus.OK);
        }

        return ErrorResponse.build(result);
    }

    @PostMapping
    public ResponseEntity<Object> addUser(@RequestBody UserRequestDTO userRequestDTO) {
        Result<User> result = service.addUser(userRequestDTO);
        if (result.isSuccess()) {
            return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
        }

        return ErrorResponse.build(result);
    }

    @PutMapping
    public ResponseEntity<Object> updateWholeUser(@RequestBody UserRequestDTO userRequestDTO) {
        Result<User> result = service.updateUser(userRequestDTO);
        if (result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return ErrorResponse.build(result);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateMoneyForUser(@PathVariable int userId, @RequestBody BigDecimal addedMoney) {
        Result<User> result = service.updateUserMoney(userId, addedMoney);
        if (result.isSuccess()) {
            return new ResponseEntity<>(result.getPayload(), HttpStatus.OK);
        }

        return ErrorResponse.build(result);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteByUserId(@RequestParam String email) {
        User user = service.findUserByEmail(email);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (service.deleteUser(user)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


}
