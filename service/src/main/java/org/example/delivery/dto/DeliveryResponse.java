package org.example.delivery.dto;

import lombok.Builder;
import lombok.Data;
import org.example.delivery.entity.Delivery;
import org.example.delivery.valueobject.Address;
import org.example.delivery.valueobject.CourierInfo;
import org.example.delivery.valueobject.DeliveryStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class DeliveryResponse {
    private UUID id;
    private String deliveryNumber;
    private String orderId;
    private DeliveryStatus status;
    private Address deliveryAddress;
    private CourierInfo courierInfo;
    private LocalDateTime estimatedDeliveryTime;
    private LocalDateTime actualDeliveryTime;
    private LocalDateTime pickupTime;
    private String recipientName;
    private String recipientPhone;
    private String deliveryNotes;
    private String signature;
    private String trackingUrl;
    private String currentLocation;
    private Integer deliveryAttempts;
    private Double deliveryFee;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static DeliveryResponse fromEntity(Delivery delivery) {
        return DeliveryResponse.builder()
                .id(delivery.getId())
                .deliveryNumber(delivery.getDeliveryNumber())
                .orderId(delivery.getOrderId())
                .status(delivery.getStatus())
                .deliveryAddress(delivery.getDeliveryAddress())
                .courierInfo(delivery.getCourierInfo())
                .estimatedDeliveryTime(delivery.getEstimatedDeliveryTime())
                .actualDeliveryTime(delivery.getActualDeliveryTime())
                .pickupTime(delivery.getPickupTime())
                .recipientName(delivery.getRecipientName())
                .recipientPhone(delivery.getRecipientPhone())
                .deliveryNotes(delivery.getDeliveryNotes())
                .signature(delivery.getSignature())
                .trackingUrl(delivery.getTrackingUrl())
                .currentLocation(delivery.getCurrentLocation())
                .deliveryAttempts(delivery.getDeliveryAttempts())
                .deliveryFee(delivery.getDeliveryFee())
                .createdAt(delivery.getCreatedAt())
                .updatedAt(delivery.getUpdatedAt())
                .build();
    }
}
