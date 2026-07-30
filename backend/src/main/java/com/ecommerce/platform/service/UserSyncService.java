package com.ecommerce.platform.service;

import com.ecommerce.platform.entity.User;
import com.ecommerce.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSyncService {

    private final UserRepository userRepository;


    public User syncUser(Jwt jwt) {

        String keycloakUserId = jwt.getSubject();


        return userRepository
                .findByKeycloakUserId(keycloakUserId)
                .orElseGet(() -> {

                    User user = new User();

                    user.setKeycloakUserId(keycloakUserId);

                    user.setUsername(
                            jwt.getClaimAsString(
                                    "preferred_username"
                            )
                    );

                    user.setEmail(
                            jwt.getClaimAsString(
                                    "email"
                            )
                    );

                    user.setEnabled(true);


                    return userRepository.save(user);
                });
    }
}