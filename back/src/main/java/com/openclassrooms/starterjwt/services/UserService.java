package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.EmailAlreadyTakenException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.exception.UnauthorizedException;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void delete(Long id, String requestingUserEmail) {
        User user = this.userRepository.findById(id).orElseThrow(NotFoundException::new);

        if (!Objects.equals(user.getEmail(), requestingUserEmail)) {
            throw new UnauthorizedException();
        }

        this.userRepository.deleteById(id);
    }

    public User findById(Long id) {
        return this.userRepository.findById(id).orElse(null);
    }

    public boolean isAdmin(String email) {
        return this.userRepository.findByEmail(email)
                .map(User::isAdmin)
                .orElse(false);
    }

    public User register(SignupRequest signUpRequest) {
        if (this.userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new EmailAlreadyTakenException("Error: Email is already taken!");
        }

        User user = new User(signUpRequest.getEmail(),
                signUpRequest.getLastName(),
                signUpRequest.getFirstName(),
                this.passwordEncoder.encode(signUpRequest.getPassword()),
                false);

        return this.userRepository.save(user);
    }
}
