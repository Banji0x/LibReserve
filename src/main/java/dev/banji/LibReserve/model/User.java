package dev.banji.LibReserve.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import static org.hibernate.annotations.CascadeType.ALL;

@Getter
//Added for the concrete subclasses to have getter's.
//This is not necessary because the subclasses have direct access to the fields {protected modifier}.
@MappedSuperclass
public abstract class User {
    protected String firstName;
    protected String middleName;
    protected String lastName;
    protected String gender;

    @Column(nullable = false)
    @Setter
    protected String password;
    @Column(unique = true, nullable = false)
    protected String phoneNumber;
    @Column(unique = true, nullable = false)
    protected String emailAddress;
    protected String lga;
    protected String state;
    protected String country;
    @OneToOne
    @Cascade(ALL)
    protected Account account;
}
