package dev.banji.LibReserve.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor(access = PRIVATE)
public class Account {
    @Id
    @GeneratedValue
    private Long id;
    private boolean isEnabled;
    private boolean notLocked;

    public Account(boolean isEnabled, boolean notLocked) {
        this.isEnabled = isEnabled;
        this.notLocked = notLocked;
    }
}
