package dev.banji.LibReserve.config.security;

import dev.banji.LibReserve.model.Librarian;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class SecurityLibrarian implements UserDetails {
    private final Librarian librarian;

    public SecurityLibrarian(Librarian librarian) {
        this.librarian = librarian;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_LIBRARIAN");
    }

    @Override
    public String getPassword() {
        return librarian.getPassword();
    }

    @Override
    public String getUsername() {
        return librarian.getFirstName() + " " + librarian.getLastName();
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
}
