package com.seucodigo.fecharcaixa.model;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void whenCreateUser_thenPropertiesAreSet() {
        User user = new User();
        user.setId("1");
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRole(User.Role.ADMIN);
        user.setActive(true);

        assertEquals("1", user.getId());
        assertEquals("Test User", user.getName());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("testuser", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals(User.Role.ADMIN, user.getRole());
        assertTrue(user.isActive());
    }

    @Test
    void whenGetAuthorities_thenRoleIsCorrect() {
        User user = new User();
        user.setRole(User.Role.ADMIN);

        var authorities = user.getAuthorities();
        assertEquals(1, authorities.size());
        
        GrantedAuthority authority = authorities.iterator().next();
        assertEquals("ROLE_ADMIN", authority.getAuthority());
    }

    @Test
    void whenUserDetailsMethodsCalled_thenDefaultValuesAreReturned() {
        User user = new User();
        user.setActive(true);

        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
    }
} 