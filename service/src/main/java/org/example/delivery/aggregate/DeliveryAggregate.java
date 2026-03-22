package org.example.delivery.aggregate;

import lombok.Getter;
import org.example.delivery.entity.Delivery;
import org.example.delivery.valueobject.CourierInfo;
import org.example.delivery.valueobject.DeliveryStatus;

import java.time.LocalDateTime;

@Getter
public class DeliveryAggregate {
    private final Delivery delivery;
    private boolean canBeAssigned;
    private boolean canBePickedUp;
    private boolean canBeDelivered;
    private boolean canBeCancelled;
    private boolean canBeReturned;

    public DeliveryAggregate(Delivery delivery) {
        this.delivery = delivery;
        validateState();
    }

    private void validateState() {
        this.canBeAssigned = delivery.getStatus() == DeliveryStatus.PENDING;
        this.canBePickedUp = delivery.getStatus() == DeliveryStatus.ASSIGNED;
        this.canBeDelivered = delivery.getStatus() == DeliveryStatus.OUT_FOR_DELIVERY;
        this.canBeCancelled = delivery.getStatus() == DeliveryStatus.PENDING ||
                delivery.getStatus() == DeliveryStatus.ASSIGNED;
        this.canBeReturned = delivery.getStatus() == DeliveryStatus.ATTEMPTED;
    }

    public DeliveryAggregate assignCourier(CourierInfo courierInfo) {
        if (!canBeAssigned) {
            throw new IllegalStateException("Delivery cannot be assigned. Current status: " +
                    delivery.getStatus());
        }

        Delivery assignedDelivery = Delivery.builder()
                .id(delivery.getId())
                .deliveryNumber(delivery.getDeliveryNumber())
                .orderId(delivery.getOrderId())
                .status(DeliveryStatus.ASSIGNED)
                .deliveryAddress(delivery.getDeliveryAddress())
                .courierInfo(courierInfo)
                .estimatedDeliveryTime(delivery.getEstimatedDeliveryTime())
                .recipientName(delivery.getRecipientName())
                .recipientPhone(delivery.getRecipientPhone())
                .deliveryNotes(delivery.getDeliveryNotes())
                .trackingUrl(generateTrackingUrl())
                .deliveryAttempts(0)
                .deliveryFee(delivery.getDeliveryFee())
                .build();

        return new DeliveryAggregate(assignedDelivery);
    }

    public DeliveryAggregate pickUp() {
        if (!canBePickedUp) {
            throw new IllegalStateException("Delivery cannot be picked up. Current status: " +
                    delivery.getStatus());
        }

        Delivery pickedUpDelivery = Delivery.builder()
                .id(delivery.getId())
                .deliveryNumber(delivery.getDeliveryNumber())
                .orderId(delivery.getOrderId())
                .status(DeliveryStatus.PICKED_UP)
                .deliveryAddress(delivery.getDeliveryAddress())
                .courierInfo(delivery.getCourierInfo())
                .estimatedDeliveryTime(delivery.getEstimatedDeliveryTime())
                .pickupTime(LocalDateTime.now())
                .recipientName(delivery.getRecipientName())
                .recipientPhone(delivery.getRecipientPhone())
                .deliveryNotes(delivery.getDeliveryNotes())
                .trackingUrl(delivery.getTrackingUrl())
                .deliveryAttempts(delivery.getDeliveryAttempts())
                .deliveryFee(delivery.getDeliveryFee())
                .build();

        return new DeliveryAggregate(pickedUpDelivery);
    }

    public DeliveryAggregate startDelivery() {
        if (delivery.getStatus() != DeliveryStatus.PICKED_UP) {
            throw new IllegalStateException("Delivery cannot start. Current status: " +
                    delivery.getStatus());
        }

        Delivery inTransitDelivery = Delivery.builder()
                .id(delivery.getId())
                .deliveryNumber(delivery.getDeliveryNumber())
                .orderId(delivery.getOrderId())
                .status(DeliveryStatus.IN_TRANSIT)
                .deliveryAddress(delivery.getDeliveryAddress())
                .courierInfo(delivery.getCourierInfo())
                .estimatedDeliveryTime(delivery.getEstimatedDeliveryTime())
                .pickupTime(delivery.getPickupTime())
                .recipientName(delivery.getRecipientName())
                .recipientPhone(delivery.getRecipientPhone())
                .deliveryNotes(delivery.getDeliveryNotes())
                .trackingUrl(delivery.getTrackingUrl())
                .deliveryAttempts(delivery.getDeliveryAttempts())
                .deliveryFee(delivery.getDeliveryFee())
                .build();

        return new DeliveryAggregate(inTransitDelivery);
    }

    public DeliveryAggregate outForDelivery() {
        if (delivery.getStatus() != DeliveryStatus.IN_TRANSIT) {
            throw new IllegalStateException("Delivery cannot be out for delivery. Current status: " +
                    delivery.getStatus());
        }

        Delivery outForDeliveryDelivery = Delivery.builder()
                .id(delivery.getId())
                .deliveryNumber(delivery.getDeliveryNumber())
                .orderId(delivery.getOrderId())
                .status(DeliveryStatus.OUT_FOR_DELIVERY)
                .deliveryAddress(delivery.getDeliveryAddress())
                .courierInfo(delivery.getCourierInfo())
                .estimatedDeliveryTime(delivery.getEstimatedDeliveryTime())
                .pickupTime(delivery.getPickupTime())
                .recipientName(delivery.getRecipientName())
                .recipientPhone(delivery.getRecipientPhone())
                .deliveryNotes(delivery.getDeliveryNotes())
                .trackingUrl(delivery.getTrackingUrl())
                .deliveryAttempts(delivery.getDeliveryAttempts())
                .deliveryFee(delivery.getDeliveryFee())
                .build();

        return new DeliveryAggregate(outForDeliveryDelivery);
    }

    public DeliveryAggregate deliver(String signature) {
        if (!canBeDelivered) {
            throw new IllegalStateException("Delivery cannot be delivered. Current status: " +
                    delivery.getStatus());
        }

        Delivery deliveredDelivery = Delivery.builder()
                .id(delivery.getId())
                .deliveryNumber(delivery.getDeliveryNumber())
                .orderId(delivery.getOrderId())
                .status(DeliveryStatus.DELIVERED)
                .deliveryAddress(delivery.getDeliveryAddress())
                .courierInfo(delivery.getCourierInfo())
                .estimatedDeliveryTime(delivery.getEstimatedDeliveryTime())
                .pickupTime(delivery.getPickupTime())
                .actualDeliveryTime(LocalDateTime.now())
                .recipientName(delivery.getRecipientName())
                .recipientPhone(delivery.getRecipientPhone())
                .deliveryNotes(delivery.getDeliveryNotes())
                .signature(signature)
                .trackingUrl(delivery.getTrackingUrl())
                .deliveryAttempts(delivery.getDeliveryAttempts() + 1)
                .deliveryFee(delivery.getDeliveryFee())
                .build();

        return new DeliveryAggregate(deliveredDelivery);
    }

    public DeliveryAggregate attemptDelivery(String notes) {
        Delivery attemptedDelivery = Delivery.builder()
                .id(delivery.getId())
                .deliveryNumber(delivery.getDeliveryNumber())
                .orderId(delivery.getOrderId())
                .status(DeliveryStatus.ATTEMPTED)
                .deliveryAddress(delivery.getDeliveryAddress())
                .courierInfo(delivery.getCourierInfo())
                .estimatedDeliveryTime(delivery.getEstimatedDeliveryTime())
                .pickupTime(delivery.getPickupTime())
                .recipientName(delivery.getRecipientName())
                .recipientPhone(delivery.getRecipientPhone())
                .deliveryNotes(notes)
                .trackingUrl(delivery.getTrackingUrl())
                .deliveryAttempts(delivery.getDeliveryAttempts() + 1)
                .deliveryFee(delivery.getDeliveryFee())
                .build();

        return new DeliveryAggregate(attemptedDelivery);
    }

    public DeliveryAggregate cancel(String reason) {
        if (!canBeCancelled) {
            throw new IllegalStateException("Delivery cannot be cancelled. Current status: " +
                    delivery.getStatus());
        }

        Delivery cancelledDelivery = Delivery.builder()
                .id(delivery.getId())
                .deliveryNumber(delivery.getDeliveryNumber())
                .orderId(delivery.getOrderId())
                .status(DeliveryStatus.CANCELLED)
                .deliveryAddress(delivery.getDeliveryAddress())
                .courierInfo(delivery.getCourierInfo())
                .estimatedDeliveryTime(delivery.getEstimatedDeliveryTime())
                .recipientName(delivery.getRecipientName())
                .recipientPhone(delivery.getRecipientPhone())
                .deliveryNotes(reason)
                .trackingUrl(delivery.getTrackingUrl())
                .deliveryAttempts(delivery.getDeliveryAttempts())
                .deliveryFee(delivery.getDeliveryFee())
                .build();

        return new DeliveryAggregate(cancelledDelivery);
    }

    public DeliveryAggregate returnDelivery() {
        if (!canBeReturned) {
            throw new IllegalStateException("Delivery cannot be returned. Current status: " +
                    delivery.getStatus());
        }

        Delivery returnedDelivery = Delivery.builder()
                .id(delivery.getId())
                .deliveryNumber(delivery.getDeliveryNumber())
                .orderId(delivery.getOrderId())
                .status(DeliveryStatus.RETURNED)
                .deliveryAddress(delivery.getDeliveryAddress())
                .courierInfo(delivery.getCourierInfo())
                .estimatedDeliveryTime(delivery.getEstimatedDeliveryTime())
                .recipientName(delivery.getRecipientName())
                .recipientPhone(delivery.getRecipientPhone())
                .deliveryNotes(delivery.getDeliveryNotes())
                .trackingUrl(delivery.getTrackingUrl())
                .deliveryAttempts(delivery.getDeliveryAttempts())
                .deliveryFee(delivery.getDeliveryFee())
                .build();

        return new DeliveryAggregate(returnedDelivery);
    }

    public DeliveryAggregate updateLocation(String location) {
        Delivery updatedLocationDelivery = Delivery.builder()
                .id(delivery.getId())
                .deliveryNumber(delivery.getDeliveryNumber())
                .orderId(delivery.getOrderId())
                .status(delivery.getStatus())
                .deliveryAddress(delivery.getDeliveryAddress())
                .courierInfo(delivery.getCourierInfo())
                .estimatedDeliveryTime(delivery.getEstimatedDeliveryTime())
                .pickupTime(delivery.getPickupTime())
                .actualDeliveryTime(delivery.getActualDeliveryTime())
                .recipientName(delivery.getRecipientName())
                .recipientPhone(delivery.getRecipientPhone())
                .deliveryNotes(delivery.getDeliveryNotes())
                .signature(delivery.getSignature())
                .trackingUrl(delivery.getTrackingUrl())
                .currentLocation(location)
                .deliveryAttempts(delivery.getDeliveryAttempts())
                .deliveryFee(delivery.getDeliveryFee())
                .build();

        return new DeliveryAggregate(updatedLocationDelivery);
    }

    private String generateTrackingUrl() {
        return "https://tracking.delivery-service.com/" + delivery.getDeliveryNumber();
    }
}
