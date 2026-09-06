package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;

public interface AuthService {
    boolean isAdmin(String email);

    User register(SignupRequest signUpRequest);
}
