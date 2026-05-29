package com.example.service;

import com.example.exception.UserNotFoundException;
import com.example.model.User;
import com.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
    }

    @Test
    void shouldFindAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(new User("Alice", "alice@test.com")));
        assertThat(userService.findAll()).hasSize(1);
    }

    @Test
    void shouldFindUserById() {
        User user = new User("Bob", "bob@test.com");
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThat(userService.findById(1L).getName()).isEqualTo("Bob");
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.findById(99L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void shouldCreateUser() {
        User user = new User("Charlie", "charlie@test.com");
        when(userRepository.existsByEmail("charlie@test.com")).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);

        assertThat(userService.create(user).getName()).isEqualTo("Charlie");
    }

    @Test
    void shouldThrowWhenEmailExists() {
        User user = new User("Charlie", "charlie@test.com");
        when(userRepository.existsByEmail("charlie@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("charlie@test.com");
    }

    @Test
    void shouldDeleteUser() {
        when(userRepository.existsById(1L)).thenReturn(true);
        userService.delete(1L);
        verify(userRepository).deleteById(1L);
    }
}
