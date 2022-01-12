package com.vimol.issuetracker.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vimol.issuetracker.entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class UserPrincipal implements UserDetails {
    private Long id;
    private String firstName;
    private String lastName;

    @JsonIgnore
    private String email;
    @JsonIgnore
    private String password;
    private User.UserType type;
    
    private final Set<GrantedAuthority> authoritires = new HashSet<>();

    public UserPrincipal(Long id, String firstName, String email,
                         String password, User.UserType type) {
        this.id = id;
        this.firstName = firstName;
        this.email = email;
        this.password = password;
        this.type = type;

        authoritires.add(new SimpleGrantedAuthority("ADMIN"));
        authoritires.add(new SimpleGrantedAuthority("CLIENT"));
        authoritires.add(new SimpleGrantedAuthority("AGENT"));

    }

	public void setId(Long id) {
        this.id = id;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public static UserPrincipal create(User user) {
        return new UserPrincipal(user.get_id(),
                user.getFirstName(), user.getEmail(), user.getPassword(),  user.getPosition());
    }

    public Long getId() {
        return id;
    }
    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return this.email;
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
    public boolean equals(Object otherUser) {
        if(otherUser == null) return false;
        else if (!(otherUser instanceof UserDetails)) return false;
        else return (otherUser.hashCode() == hashCode());
    }
    @Override
    public int hashCode() {

        return this.email.hashCode();
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authoritires;
    }

}
