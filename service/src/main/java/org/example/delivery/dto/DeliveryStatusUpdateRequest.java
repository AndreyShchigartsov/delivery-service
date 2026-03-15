package org.example.delivery.dto;

import lombok.Data;
import org.example.delivery.valueobject.CourierInfo;

@Data
public class DeliveryStatusUpdateRequest {
    private String status;
    private CourierInfo courierInfo;
    private String signature;
    private String notes;
    private String location;
}
