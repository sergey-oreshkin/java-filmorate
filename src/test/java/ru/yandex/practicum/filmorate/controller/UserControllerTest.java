package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc

public class UserControllerTest {

    private User validUser;

    private final int invalidId = 100;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserStorage userStorage;

    @BeforeEach
    void reset() {
        validUser = new User(
                0,
                "email@mail.ru",
                "login",
                "user",
                LocalDate.of(1979, Month.SEPTEMBER, 17)
        );
        userStorage.clear();
    }

    @Test
    void test_1_createValidUserResponseShouldBeOk() throws Exception {
        validUser.setId(0);
        String body = mapper.writeValueAsString(validUser);

        this.mockMvc.perform(post("/users").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void test_2_updateValidUserResponseShouldBeOk() throws Exception {
        userStorage.create(validUser);
        validUser.setName("new");
        String body = mapper.writeValueAsString(validUser);

        this.mockMvc.perform(put("/users").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @MethodSource("invalidUsersSource")
    @ParameterizedTest(name = "{0}")
    void test_3_createInvalidUserResponseShouldBeBadRequest(String name, User user) throws Exception {
        String body = mapper.writeValueAsString(user);

        this.mockMvc.perform(post("/users").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isBadRequest());
    }

    @Test
    void test_4_getUserByIdTest() throws Exception {
        userStorage.create(validUser);

        this.mockMvc.perform(get(format("/users/%d", invalidId)))
                .andExpect(status().isNotFound());

        this.mockMvc.perform(get(format("/users/%d", validUser.getId())))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(validUser)));
    }

    @Test
    void test_5_addFriendTest() throws Exception {

        User friend = new User(
                0,
                "email@mail.ru",
                "login",
                "friend",
                LocalDate.of(1979, Month.SEPTEMBER, 17)
        );

        userStorage.create(validUser);
        userStorage.create(friend);

        this.mockMvc.perform(put(format("/users/%d/friends/%d", invalidId, friend.getId())))
                .andExpect(status().isNotFound());

        this.mockMvc.perform(put(format("/users/%d/friends/%d", validUser.getId(), invalidId)))
                .andExpect(status().isNotFound());

        this.mockMvc.perform(put(format("/users/%d/friends/%d", validUser.getId(), friend.getId())))
                .andExpect(status().isOk());

        assertEquals(Set.of(friend.getId()), validUser.getFriends(), "Friend not added to user friends list");

        assertEquals(Set.of(validUser.getId()), friend.getFriends(), "User not added to friend friends list");
    }

    @Test
    void test_6_deleteFriendTest() throws Exception {
        int invalidId = 100;
        User friend = new User(
                0,
                "email@mail.ru",
                "login",
                "friend",
                LocalDate.of(1979, Month.SEPTEMBER, 17)
        );
        userStorage.create(validUser);
        userStorage.create(friend);
        validUser.addFriend(friend.getId());

        this.mockMvc.perform(delete(format("/users/%d/friends/%d", invalidId, friend.getId())))
                .andExpect(status().isNotFound());

        this.mockMvc.perform(delete(format("/users/%d/friends/%d", validUser.getId(), invalidId)))
                .andExpect(status().isNotFound());

        this.mockMvc.perform(delete(format("/users/%d/friends/%d", validUser.getId(), friend.getId())))
                .andExpect(status().isOk());

        assertTrue(validUser.getFriends().isEmpty(), "Friend of user is not deleted");

        assertTrue(friend.getFriends().isEmpty(), "User not deleted from friend friends list");
    }

    @Test
    void test_7_getFriendsListTest() throws Exception {
        int invalidId = 100;
        User friend = new User(
                0,
                "email@mail.ru",
                "login",
                "friend",
                LocalDate.of(1979, Month.SEPTEMBER, 17)
        );
        userStorage.create(validUser);
        userStorage.create(friend);
        validUser.addFriend(friend.getId());

        this.mockMvc.perform(get(format("/users/%d/friends", invalidId)))
                .andExpect(status().isNotFound());

        this.mockMvc.perform(get(format("/users/%d/friends", validUser.getId())))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(friend))));
    }

    @Test
    void test_8_getCommonFriendsListTest() throws Exception {
        int invalidId = 100;
        User anotherUser = new User(
                0,
                "email@mail.ru",
                "login",
                "friend",
                LocalDate.of(1979, Month.SEPTEMBER, 17)
        );
        User commonFriend = new User(
                0,
                "email@mail.ru",
                "login",
                "friend",
                LocalDate.of(1979, Month.SEPTEMBER, 17)
        );
        userStorage.create(validUser);
        userStorage.create(anotherUser);
        userStorage.create(commonFriend);
        validUser.addFriend(commonFriend.getId());
        anotherUser.addFriend(commonFriend.getId());

        this.mockMvc.perform(get(format("/users/%d/friends/common/%d", invalidId, anotherUser.getId())))
                .andExpect(status().isNotFound());

        this.mockMvc.perform(get(format("/users/%d/friends/common/%d", validUser.getId(), invalidId)))
                .andExpect(status().isNotFound());

        this.mockMvc.perform(get(format("/users/%d/friends/common/%d", validUser.getId(), anotherUser.getId())))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(commonFriend))));

    }

    private static Stream<Arguments> invalidUsersSource() {
        int i = 0;
        return Stream.of(
                Arguments.of("Empty email",
                        new User(
                                ++i,
                                "",
                                "login",
                                "name",
                                LocalDate.of(1979, Month.SEPTEMBER, 17)
                        )),
                Arguments.of("Bad email",
                        new User(
                                ++i,
                                "@mail.ru",
                                "login",
                                "name",
                                LocalDate.of(1979, Month.SEPTEMBER, 17)
                        )),
                Arguments.of("Empty login",
                        new User(
                                ++i,
                                "mail@mail.ru",
                                "",
                                "name",
                                LocalDate.of(1979, Month.SEPTEMBER, 17)
                        )),
                Arguments.of("Login with spaces",
                        new User(
                                ++i,
                                "mail@mail.ru",
                                "lo gin",
                                "name",
                                LocalDate.of(1979, Month.SEPTEMBER, 17)
                        )),
                Arguments.of("Birthday in the future",
                        new User(
                                ++i,
                                "mail@mail.ru",
                                "login",
                                "name",
                                LocalDate.of(2079, Month.SEPTEMBER, 17)
                        ))
        );
    }
}
