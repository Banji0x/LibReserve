package dev.banji.LibReserve.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = PRIVATE)
public class Librarian extends User {
    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true, nullable = false)
    private String staffNumber; //since it's a university system, each staff has a unique generated number, so it being null is impossible

    public Librarian(String firstName, String middleName, String lastName, String gender,
                     String phoneNumber, String emailAddress, String lga,
                     String state, String country, Account account, String staffNumber, String password) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.lga = lga;
        this.state = state;
        this.country = country;
        this.account = account;
        this.staffNumber = staffNumber;
        this.password = password;
    }

}
