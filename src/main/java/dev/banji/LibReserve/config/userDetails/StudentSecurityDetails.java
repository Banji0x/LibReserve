package dev.banji.LibReserve.config.userDetails;

import dev.banji.LibReserve.model.Student;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class StudentSecurityDetails implements UserDetails {
    private final Student student;

    public StudentSecurityDetails(Student student) { //if the student matric number was found
        this.student = student;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_STUDENT");
    }

    @Override
    public String getPassword() {
        return student.getPassword();
    }

    @Override
    public String getUsername() {
        return student.getFirstName() + " " + student.getLastName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return student.getAccount().isNotLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return student.getAccount().isEnabled();
    }
}
