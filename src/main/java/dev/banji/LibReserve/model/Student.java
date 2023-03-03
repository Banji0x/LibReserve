package dev.banji.LibReserve.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@Entity
public class Student {
    @Id
    @GeneratedValue
    private Long id;
    @OneToMany
    private List<Reservations> reservationId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String matricNumber;
    private String department;
    private String level;
}
