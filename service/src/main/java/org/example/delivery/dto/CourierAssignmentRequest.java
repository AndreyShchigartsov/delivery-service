package org.example.delivery.dto;

import lombok.Data;
import org.example.delivery.valueobject.CourierInfo;

@Data
public class CourierAssignmentRequest {
    private CourierInfo courierInfo;
}
