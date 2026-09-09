package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper userMapper = new UserMapperImpl();

    @Test
    void toDto_shouldMapAllFields() {
        // given
        User user = User.builder().id(1L).email("margot@teacher.com").lastName("Delahaye").firstName("Margot").password("encoded").admin(true).build();

        // when
        UserDto dto = userMapper.toDto(user);

        // then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getEmail()).isEqualTo("margot@teacher.com");
        assertThat(dto.isAdmin()).isTrue();
    }

    @Test
    void toEntity_shouldMapAllFields() {
        // given
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setEmail("margot@teacher.com");
        dto.setLastName("Delahaye");
        dto.setFirstName("Margot");
        dto.setPassword("encoded");
        dto.setAdmin(true);

        // when
        User user = userMapper.toEntity(dto);

        // then
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getEmail()).isEqualTo("margot@teacher.com");
        assertThat(user.isAdmin()).isTrue();
    }

    @Test
    void toDto_shouldReturnNull_whenUserIsNull() {
        // given / when
        UserDto dto = userMapper.toDto((User) null);

        // then
        assertThat(dto).isNull();
    }

    @Test
    void toEntity_shouldReturnNull_whenDtoIsNull() {
        // given / when
        User user = userMapper.toEntity((UserDto) null);

        // then
        assertThat(user).isNull();
    }

    @Test
    void toDto_shouldReturnNull_whenListIsNull() {
        // given / when
        List<UserDto> result = userMapper.toDto((List<User>) null);

        // then
        assertThat(result).isNull();
    }

    @Test
    void toDto_shouldMapList_whenGivenUsers() {
        // given
        User user1 = User.builder().id(1L).email("margot@teacher.com").lastName("Delahaye").firstName("Margot").password("encoded").admin(false).build();
        User user2 = User.builder().id(2L).email("john@mail.com").lastName("Doe").firstName("John").password("encoded").admin(false).build();

        // when
        List<UserDto> result = userMapper.toDto(List.of(user1, user2));

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getEmail()).isEqualTo("margot@teacher.com");
    }

    @Test
    void toEntity_shouldReturnNull_whenListIsNull() {
        // given / when
        List<User> result = userMapper.toEntity((List<UserDto>) null);

        // then
        assertThat(result).isNull();
    }

    @Test
    void toEntity_shouldMapList_whenGivenDtos() {
        // given
        UserDto dto1 = new UserDto();
        dto1.setId(1L);
        dto1.setEmail("margot@teacher.com");
        dto1.setLastName("Delahaye");
        dto1.setFirstName("Margot");
        dto1.setPassword("encoded");
        dto1.setAdmin(false);

        // when
        List<User> result = userMapper.toEntity(List.of(dto1));

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("margot@teacher.com");
    }
}
