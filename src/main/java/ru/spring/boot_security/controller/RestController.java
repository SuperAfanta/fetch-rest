package ru.spring.boot_security.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.spring.boot_security.dto.UserDTO;
import ru.spring.boot_security.model.Role;
import ru.spring.boot_security.model.User;
import ru.spring.boot_security.service.UserService;
import ru.spring.boot_security.util.UserErrorResponse;
import ru.spring.boot_security.util.UserNotCreatedException;
import ru.spring.boot_security.util.UserNotFoundException;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/api")
public class RestController {

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public RestController(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @GetMapping("/people")
    public List<UserDTO> getUsers() {
        return userService.getUsersList().stream().map(this::convertToUserDTO).toList();
    }

    @GetMapping("/people/{id}")
    public UserDTO getUser(@PathVariable("id") Long id) {
        return convertToUserDTO(userService.getUser(id));
    }

    @DeleteMapping("/people/{id}")
    public void deleteUser(@PathVariable("id") Long id) {
        userService.delete(id);
    }

    @PostMapping("/people")
    public Long addUser(@RequestBody @Valid UserDTO userDto,
                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMsg.append(error.getField())
                        .append(" - ").append(error.getDefaultMessage())
                        .append(";");
                System.out.println(errorMsg);
                throw new UserNotCreatedException(errorMsg.toString());
            }
        }
        User user = convertToUser(userDto);
        userService.add(user);
        return user.getId();
    }

    @PatchMapping("/people")
    public ResponseEntity<HttpStatus> updateUser(@RequestBody @Valid UserDTO userDto,
                                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMsg.append(error.getField())
                        .append(" - ").append(error.getDefaultMessage())
                        .append(";");
                System.out.println(errorMsg);
                throw new UserNotCreatedException(errorMsg.toString());
            }
        }
        userService.update(convertToUser(userDto));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/authUser")
    public UserDTO getAuthUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return convertToUserDTO(user);
    }

    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleException(UserNotFoundException ignored) {
        UserErrorResponse response = new UserErrorResponse( "Человек с таким айди не найден");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleNotCreatedException(UserNotCreatedException e) {
        UserErrorResponse response = new UserErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private User convertToUser(UserDTO userDto) {
        System.out.println(userDto);
        Set<Role> roleSet = userDto.getRoleSet().stream().map(Role::getRole).collect(Collectors.toSet());
        System.out.println(Role.getRole("USER"));
        System.out.println(roleSet);
        return new User(userDto.getId(), userDto.getUsername(), bCryptPasswordEncoder.encode(userDto.getPass()), roleSet);
    }

    private UserDTO convertToUserDTO(User user) {
        Set<String> roleSet = user.getRoleSet().stream().map(Role::getValue).collect(Collectors.toSet());
        return new UserDTO(user.getId(), user.getUsername(), user.getPass(), roleSet);
    }


}
