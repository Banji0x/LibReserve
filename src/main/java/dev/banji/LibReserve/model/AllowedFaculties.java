package dev.banji.LibReserve.model;

public record AllowedFaculties(String name, AllowedDepartments[] departments) {
    public record AllowedDepartments(String name) {
    }
}