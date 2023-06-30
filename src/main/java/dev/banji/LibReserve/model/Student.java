package dev.banji.LibReserve.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static org.hibernate.annotations.CascadeType.ALL;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = PRIVATE)
public class Student extends User {
    @Id
    @GeneratedValue
    private Long id;
    @OneToMany
    @Cascade(ALL)
    private List<Reservation> reservationList;
    private String matricNumber;
    private String department;
    private String level;

    public Student(String firstName, String middleName, String lastName, String gender,
                   String phoneNumber, String emailAddress, String lga,
                   String state, String country, Account account,
                   List<Reservation> reservationList,
                   String matricNumber, String department, String level, String password) {
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
        this.reservationList = reservationList;
        this.matricNumber = matricNumber;
        this.department = department;
        this.level = level;
        this.password = password;
    }
}