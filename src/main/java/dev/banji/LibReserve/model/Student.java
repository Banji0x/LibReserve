package dev.banji.LibReserve.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = PRIVATE)
public class Student extends User {
    @Id
    @GeneratedValue
    @Column(name = "student_id")
    private Long id;
    @OneToMany(mappedBy = "student", cascade = CascadeType.PERSIST)
    private List<StudentReservation> studentReservationList;
    private String matricNumber;
    private String department;
    private String level;

    public Student(String firstName, String middleName, String lastName, String gender,
                   String phoneNumber, String emailAddress, String lga,
                   String state, String country, Account account,
                   List<StudentReservation> studentReservationList,
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
        this.studentReservationList = studentReservationList;
        this.matricNumber = matricNumber;
        this.department = department;
        this.level = level;
        this.password = password;
    }
}