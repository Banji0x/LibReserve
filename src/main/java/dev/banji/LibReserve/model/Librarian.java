package dev.banji.LibReserve.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@SuppressWarnings("JpaObjectClassSignatureInspection") // to disable IDE warnings for a PUBLIC No-Args Constructor.
@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Librarian {
    @Id
    @GeneratedValue
    private Long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String gender;
    @Column(unique = true, nullable = false)
    private String phoneNumber;
    @Column(unique = true, nullable = false)
    private String emailAddress;
    private String lga;
    private String state;
    private String country;
    @Column(unique = true, nullable = false)
    private String staffNumber; //since it's a university system, each staff has a unique generated number
    private String password;
}
