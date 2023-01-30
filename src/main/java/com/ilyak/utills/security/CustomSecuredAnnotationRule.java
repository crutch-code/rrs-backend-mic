package com.ilyak.utills.security;

import com.ilyak.service.UserLogoutService;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecuredAnnotationRule;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.security.rules.SecurityRuleResult;
import io.micronaut.security.token.RolesFinder;
import io.micronaut.web.router.MethodBasedRouteMatch;
import io.micronaut.web.router.RouteMatch;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Singleton
@Replaces(SecuredAnnotationRule.class)
public class CustomSecuredAnnotationRule extends SecuredAnnotationRule {
    /**
     * @param rolesFinder Roles Parser
     */

    @Inject
    UserLogoutService userLogoutService;

    public CustomSecuredAnnotationRule(RolesFinder rolesFinder) {
        super(rolesFinder);
    }

    @Override
    public Publisher<SecurityRuleResult> check(HttpRequest<?> request, RouteMatch<?> routeMatch, Authentication authentication) {
        if (routeMatch instanceof MethodBasedRouteMatch) {
            MethodBasedRouteMatch<?, ?> methodRoute = ((MethodBasedRouteMatch) routeMatch);
            if (methodRoute.hasAnnotation(Secured.class)) {
                Optional<String[]> optionalValue = methodRoute.getValue(Secured.class, String[].class);
                if (optionalValue.isPresent()) {
                    List<String> values = Arrays.asList(optionalValue.get());
                    String bearerToken = String.valueOf(request.getHeaders().getAuthorization().orElse(null));
                    if (values.contains(SecurityRule.DENY_ALL)) {
                        return Flowable.just(SecurityRuleResult.REJECTED);
                    }
//                    if(userLogoutService.isLogout(bearerToken))
//                        return Flowable.just(SecurityRuleResult.UNKNOWN);
                    return compareRoles(values, getRoles(authentication));
                }
            }
        }
        return Flowable.just(SecurityRuleResult.UNKNOWN);
    }
}
