package ru.itmo.securitylab.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.securitylab.dto.LoginRequest;
import ru.itmo.securitylab.dto.TokenResponse;
import ru.itmo.securitylab.entity.User;
import ru.itmo.securitylab.exception.InvalidCredentialsException;
import ru.itmo.securitylab.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder passwords;
    private final TokenService tokens;

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        User user = users.findByUsername(request.username()).orElseThrow(InvalidCredentialsException::new);
        if (!passwords.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        return new TokenResponse(tokens.issue(user), "Bearer");
    }
}
