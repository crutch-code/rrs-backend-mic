package com.ilyak.utills.security;

import com.ilyak.entity.User;
import com.ilyak.repository.UserRepository;
import com.ilyak.utills.security.responses.CustomAuthResponse;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.reactivex.rxjava3.core.Flowable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;


@Singleton
public class AuthProviderUser implements AuthenticationProvider {

    @Inject
    UserRepository userRepository;

    @Override
    public Publisher<AuthenticationResponse> authenticate(HttpRequest<?> httpRequest, AuthenticationRequest<?, ?> authenticationRequest) {

        User user = userRepository.findByUserNickName(String.valueOf(authenticationRequest.getIdentity()))
                .orElse(
                        userRepository.findByUserEmail(String.valueOf(authenticationRequest.getIdentity()))
                                .orElse(
                                        userRepository.findByUserPhoneNumber(String.valueOf(authenticationRequest.getIdentity())).orElse(null)
                                )
                );
        if(user == null)
            return Flowable.just(AuthenticationResponse.failure("User not found"));

        if (!user.getUserPassword().equals(authenticationRequest.getSecret()))
            return Flowable.just(AuthenticationResponse.failure("Incorrect Password"));

        return Flowable.just(new CustomAuthResponse(user));
    }


}
