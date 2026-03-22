package org.example.delivery.dto;

import lombok.Data;
import org.example.delivery.valueobject.Address;

import java.time.LocalDateTime;

@Data
public class DeliveryRequest {
    private String orderId;
    private Address deliveryAddress;
    private LocalDateTime estimatedDeliveryTime;
    private String recipientName;
    private String recipientPhone;
    private String deliveryNotes;
    private Double deliveryFee;
}
