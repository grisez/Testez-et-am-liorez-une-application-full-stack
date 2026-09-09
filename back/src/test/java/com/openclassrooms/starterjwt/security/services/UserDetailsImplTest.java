package com.openclassrooms.starterjwt.security.services;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserDetailsImplTest {

    @Test
    void equals_shouldReturnTrue_whenSameInstance() {
        // given
        UserDetailsImpl user = UserDetailsImpl.builder().id(1L).username("margot@teacher.com").build();

        // when / then
        assertThat(user.equals(user)).isTrue();
    }

    @Test
    void equals_shouldReturnFalse_whenComparedToNull() {
        // given
        UserDetailsImpl user = UserDetailsImpl.builder().id(1L).username("margot@teacher.com").build();

        // when / then
        assertThat(user.equals(null)).isFalse();
    }

    @Test
    void equals_shouldReturnFalse_whenComparedToDifferentClass() {
        // given
        UserDetailsImpl user = UserDetailsImpl.builder().id(1L).username("margot@teacher.com").build();

        // when / then
        assertThat(user.equals("not a UserDetailsImpl")).isFalse();
    }

    @Test
    void equals_shouldReturnTrue_whenIdsAreEqual() {
        // given
        UserDetailsImpl user1 = UserDetailsImpl.builder().id(1L).username("margot@teacher.com").build();
        UserDetailsImpl user2 = UserDetailsImpl.builder().id(1L).username("different@mail.com").build();

        // when / then
        assertThat(user1.equals(user2)).isTrue();
    }

    @Test
    void equals_shouldReturnFalse_whenIdsAreDifferent() {
        // given
        UserDetailsImpl user1 = UserDetailsImpl.builder().id(1L).username("margot@teacher.com").build();
        UserDetailsImpl user2 = UserDetailsImpl.builder().id(2L).username("margot@teacher.com").build();

        // when / then
        assertThat(user1.equals(user2)).isFalse();
    }
}
