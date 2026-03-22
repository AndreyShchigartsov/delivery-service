package org.example.delivery.valueobject;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourierInfo {
    private String courierId;
    private String courierName;
    private String courierPhone;
    private String vehicleType;
    private String vehicleNumber;
}
