package com.project.demo.rest.user;

import com.project.demo.logic.entity.rol.Role;
import com.project.demo.logic.entity.rol.RoleEnum;
import com.project.demo.logic.entity.rol.RoleRepository;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserRestController {
    @Autowired
    private UserRepository UserRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public List<User> getAllUsers() {
        return UserRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public User createAdministrator(@RequestBody User newAdminUser) {
        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.SUPER_ADMIN);

        if (optionalRole.isEmpty()) {
            return null;
        }

        var user = new User();
        user.setName(newAdminUser.getName());
        user.setEmail(newAdminUser.getEmail());
        user.setPassword(passwordEncoder.encode(newAdminUser.getPassword()));
        user.setRole(optionalRole.get());

        return UserRepository.save(user);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return UserRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    @GetMapping("/filterByName/{name}")
    public List<User> getUserById(@PathVariable String name) {
        return UserRepository.findUsersWithCharacterInName(name);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        return UserRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setName(user.getName());
                    existingUser.setLastname(user.getLastname());
                    existingUser.setEmail(user.getEmail());
                    return UserRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    user.setId(id);
                    return UserRepository.save(user);
                });
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        UserRepository.deleteById(id);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public User authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

}