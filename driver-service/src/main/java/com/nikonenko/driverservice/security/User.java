package com.nikonenko.driverservice.security;

import com.nikonenko.driverservice.utils.SecurityList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails, OAuth2User {

    private UUID id;

    private String username;

    private String phone;

    private String email;

    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public Object getAttribute(String name) {
        return getAttributes().get(name);
    }

    @Override
    public Map<String, Object> getAttributes() {
        Map<String, Object> map = new HashMap<>();
        putIfNotNull(map, SecurityList.ID, id);
        putIfNotNull(map, SecurityList.USERNAME, username);
        putIfNotNull(map, SecurityList.EMAIL, email);
        putIfNotNull(map, SecurityList.PHONE, phone);
        return map;
    }

    private void putIfNotNull(Map<String, Object> map, String key, Object value) {
        if (value != null) {
            map.put(key, value);
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return null;
    }
}

