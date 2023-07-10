package dev.banji.LibReserve.config.userDetails;

import dev.banji.LibReserve.model.Librarian;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class LibrarianSecurityDetails implements UserDetails {
    private final Librarian librarian;

    public LibrarianSecurityDetails(Librarian librarian) {
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
    } // when a user account is no longer valid maybe due to the account being inactive

    @Override
    public boolean isAccountNonLocked() {
        return librarian.getAccount().isNotLocked();
    } //makes sense to write logic for this when an account has been locked due to different reasons.

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    } //it only makes sense to write logic for this when you have different columns representing old and new columns in a database...

    @Override
    public boolean isEnabled() {
        return librarian.getAccount().isEnabled();
    } // only makes sense to write logic for this when an account has been disabled or maybe due to a staff leaving the company
}
