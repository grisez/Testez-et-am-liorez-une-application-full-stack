package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.User;

public interface UserService {
    void delete(Long id, String requestingUserEmail);

    User findById(Long id);
}
