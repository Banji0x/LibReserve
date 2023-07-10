package dev.banji.LibReserve.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalTime;

@Converter
public class LocalTimeToHourConverter implements AttributeConverter<String, Integer> {
    @Override
    public Integer convertToDatabaseColumn(String attribute) {
        return LocalTime.parse(attribute).getHour();
    }

    @Override
    public String convertToEntityAttribute(Integer hour) {
        return LocalTime.of(hour, 0).toString();
    }
}
