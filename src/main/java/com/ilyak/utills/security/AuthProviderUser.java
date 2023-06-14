package com.ilyak.utills.security;

import com.ilyak.entity.jpa.User;
import com.ilyak.repository.UserRepository;
import com.ilyak.utills.security.responses.CustomAuthResponse;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationFailureReason;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.reactivex.rxjava3.core.Flowable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


@Singleton
public class AuthProviderUser implements AuthenticationProvider {

    @Inject
    UserRepository userRepository;

    @Override
    public Publisher<AuthenticationResponse> authenticate(HttpRequest<?> httpRequest, AuthenticationRequest<?, ?> authenticationRequest) {
        User user =
                userRepository.findByUserEmail(String.valueOf(authenticationRequest.getIdentity()))
                                .orElse(
                                        userRepository.findByUserPhoneNumber(String.valueOf(authenticationRequest.getIdentity())).orElse(null));
        if(user == null)
            return Flowable.just(AuthenticationResponse.failure(AuthenticationFailureReason.USER_NOT_FOUND));

        if (!user.getUserPassword().equals(authenticationRequest.getSecret()))
            return Flowable.just(AuthenticationResponse.failure(AuthenticationFailureReason.CREDENTIALS_DO_NOT_MATCH));
        if(user.getIsBanned()){
            return Flowable.just(AuthenticationResponse.failure(AuthenticationFailureReason.USER_DISABLED));
        }
        return Flowable.just(
                new CustomAuthResponse(
                        user.getOid(),
                        user.getUserName(),
                        user.getIsAdmin()? List.of("IS_ADMIN") : Collections.emptyList(),
                        UUID.randomUUID().toString()
                )
        );
    }


}
