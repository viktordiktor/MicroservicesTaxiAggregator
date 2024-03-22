package com.nikonenko.paymentservice.security;

import com.nikonenko.paymentservice.utils.LogList;
import com.nikonenko.paymentservice.utils.SecurityList;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    private final JwtAuthConverterProperties properties;

    public JwtAuthConverter(JwtAuthConverterProperties properties) {
        this.properties = properties;
    }

    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        Collection<GrantedAuthority> authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                extractResourceRoles(jwt).stream()).collect(Collectors.toSet());
        User user = extractUserInfo(jwt);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                user,
                null,
                authorities
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
        return authToken;
    }

    private User extractUserInfo(Jwt jwt) {
        UUID id = UUID.fromString(jwt.getClaim(SecurityList.SUB_UUID));
        String username = jwt.getClaim(SecurityList.PREFERRED_USERNAME);
        String email = jwt.getClaim(SecurityList.EMAIL);
        String phone = jwt.getClaim(SecurityList.PHONE);
        log.info(LogList.LOG_EXTRACT_DATA, id, username, email, phone);
        return User.builder()
                .phone(phone)
                .id(id)
                .email(email)
                .username(username)
                .build();
    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaim(SecurityList.RESOURCE_ACCESS);
        Map<String, Object> resource;
        Collection<String> resourceRoles;
        if (resourceAccess == null
                || (resource = (Map<String, Object>) resourceAccess.get(properties.getResourceId())) == null
                || (resourceRoles = (Collection<String>) resource.get(SecurityList.RESOURCE_ROLES)) == null) {
            return Set.of();
        }
        log.info(LogList.LOG_EXTRACT_ROLES, resourceRoles);
        return resourceRoles.stream()
                .map(role -> new SimpleGrantedAuthority(SecurityList.ROLE_PREFIX + role))
                .collect(Collectors.toSet());
    }
}
