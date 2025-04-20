package com.teamoneboxoffice;

import com.teamoneboxoffice.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class UserTest {
    private static final long TEST_ID = 1L;
    private static final String TEST_USERNAME = "testUser";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_ROLE = "admin";
    private static final boolean TEST_IS_ACTIVE = true;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(TEST_ID, TEST_USERNAME, TEST_PASSWORD, TEST_ROLE, TEST_IS_ACTIVE);
    }

    @Test
    void testUserCreationAndGetters() {
        assertEquals(TEST_USERNAME, user.getUserName(), "Username should match the test value");
        assertEquals(TEST_PASSWORD, user.getPassword(), "Password should match the test value");
        assertEquals(TEST_ROLE, user.getRole(), "Role should match the test value");
        assertEquals(TEST_IS_ACTIVE, user.isActive(), "Active status should match the test value");
    }

    @Test
    void testSetters() {
        user.setUserName("newUser");
        user.setPassword("newPassword");
        user.setRole("user");
        user.setActive(false);

        assertEquals("newUser", user.getUserName(), "Username should be updated");
        assertEquals("newPassword", user.getPassword(), "Password should be updated");
        assertEquals("user", user.getRole(), "Role should be updated");
        assertFalse(user.isActive(), "Active status should be updated");
    }
}

