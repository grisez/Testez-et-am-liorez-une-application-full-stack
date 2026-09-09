package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.User;
import org.junit.jupiter.api.Test;

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
}
