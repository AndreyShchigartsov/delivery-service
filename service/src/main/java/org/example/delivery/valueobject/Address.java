package org.example.delivery.valueobject;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private String street;
    private String house;
    private String apartment;
    private String city;
    private String postalCode;
    private String country;
    private double latitude;
    private double longitude;

    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(country).append(", ").append(city);
        if (street != null && !street.isEmpty()) {
            sb.append(", ул. ").append(street);
        }
        if (house != null && !house.isEmpty()) {
            sb.append(", д. ").append(house);
        }
        if (apartment != null && !apartment.isEmpty()) {
            sb.append(", кв. ").append(apartment);
        }
        if (postalCode != null && !postalCode.isEmpty()) {
            sb.append(", ").append(postalCode);
        }
        return sb.toString();
    }
}
