package org.example.delivery.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.delivery.aggregate.DeliveryAggregate;
import org.example.delivery.dto.CourierAssignmentRequest;
import org.example.delivery.dto.DeliveryRequest;
import org.example.delivery.dto.DeliveryResponse;
import org.example.delivery.dto.DeliveryStatusUpdateRequest;
import org.example.delivery.entity.Delivery;
import org.example.delivery.repository.DeliveryRepository;
import org.example.delivery.valueobject.DeliveryStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    @Transactional
    public DeliveryResponse createDelivery(DeliveryRequest request) {
        // Проверка на существующую доставку для заказа
        if (deliveryRepository.existsByOrderIdAndStatusNot(request.getOrderId(), DeliveryStatus.CANCELLED)) {
            throw new RuntimeException("Delivery already exists for order: " + request.getOrderId());
        }

        Delivery delivery = Delivery.builder()
                .deliveryNumber(generateDeliveryNumber())
                .orderId(request.getOrderId())
                .status(DeliveryStatus.PENDING)
                .deliveryAddress(request.getDeliveryAddress())
                .estimatedDeliveryTime(request.getEstimatedDeliveryTime())
                .recipientName(request.getRecipientName())
                .recipientPhone(request.getRecipientPhone())
                .deliveryNotes(request.getDeliveryNotes())
                .deliveryAttempts(0)
                .deliveryFee(request.getDeliveryFee())
                .build();

        Delivery savedDelivery = deliveryRepository.save(delivery);
        log.info("Delivery created: {} for order: {}", savedDelivery.getDeliveryNumber(), request.getOrderId());

        return DeliveryResponse.fromEntity(savedDelivery);
    }

    @Transactional(readOnly = true)
    public DeliveryResponse getDelivery(UUID id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found with id: " + id));
        return DeliveryResponse.fromEntity(delivery);
    }

    @Transactional(readOnly = true)
    public DeliveryResponse getDeliveryByNumber(String deliveryNumber) {
        Delivery delivery = deliveryRepository.findByDeliveryNumber(deliveryNumber)
                .orElseThrow(() -> new RuntimeException("Delivery not found with number: " + deliveryNumber));
        return DeliveryResponse.fromEntity(delivery);
    }

    @Transactional(readOnly = true)
    public List<DeliveryResponse> getDeliveriesByOrder(String orderId) {
        return deliveryRepository.findByOrderId(orderId).stream()
                .map(DeliveryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DeliveryResponse> getAllDeliveries() {
        return deliveryRepository.findAll().stream()
                .map(DeliveryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DeliveryResponse> getDeliveriesByStatus(DeliveryStatus status) {
        return deliveryRepository.findByStatus(status).stream()
                .map(DeliveryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DeliveryResponse> getDeliveriesByCourier(String courierId) {
        return deliveryRepository.findByCourierInfoCourierId(courierId).stream()
                .map(DeliveryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public DeliveryResponse assignCourier(UUID id, CourierAssignmentRequest request) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));

        DeliveryAggregate aggregate = new DeliveryAggregate(delivery);
        DeliveryAggregate updatedAggregate = aggregate.assignCourier(request.getCourierInfo());

        Delivery savedDelivery = deliveryRepository.save(updatedAggregate.getDelivery());
        log.info("Courier assigned to delivery: {}", savedDelivery.getDeliveryNumber());

        return DeliveryResponse.fromEntity(savedDelivery);
    }

    @Transactional
    public DeliveryResponse updateDeliveryStatus(UUID id, DeliveryStatusUpdateRequest request) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));

        DeliveryAggregate aggregate = new DeliveryAggregate(delivery);
        DeliveryAggregate updatedAggregate;

        switch (request.getStatus()) {
            case "PICKED_UP":
                updatedAggregate = aggregate.pickUp();
                log.info("Delivery picked up: {}", delivery.getDeliveryNumber());
                break;

            case "IN_TRANSIT":
                updatedAggregate = aggregate.startDelivery();
                log.info("Delivery in transit: {}", delivery.getDeliveryNumber());
                break;

            case "OUT_FOR_DELIVERY":
                updatedAggregate = aggregate.outForDelivery();
                log.info("Delivery out for delivery: {}", delivery.getDeliveryNumber());
                break;

            case "DELIVERED":
                updatedAggregate = aggregate.deliver(request.getSignature());
                log.info("Delivery delivered: {}", delivery.getDeliveryNumber());
                break;

            case "ATTEMPTED":
                updatedAggregate = aggregate.attemptDelivery(request.getNotes());
                log.info("Delivery attempt: {}", delivery.getDeliveryNumber());
                break;

            case "CANCELLED":
                updatedAggregate = aggregate.cancel(request.getNotes());
                log.info("Delivery cancelled: {}", delivery.getDeliveryNumber());
                break;

            case "RETURNED":
                updatedAggregate = aggregate.returnDelivery();
                log.info("Delivery returned: {}", delivery.getDeliveryNumber());
                break;

            default:
                throw new IllegalArgumentException("Invalid status update: " + request.getStatus());
        }

        // Update location if provided
        if (request.getLocation() != null && !request.getLocation().isEmpty()) {
            updatedAggregate = updatedAggregate.updateLocation(request.getLocation());
        }

        Delivery savedDelivery = deliveryRepository.save(updatedAggregate.getDelivery());
        return DeliveryResponse.fromEntity(savedDelivery);
    }

    @Transactional
    public DeliveryResponse updateLocation(UUID id, String location) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));

        DeliveryAggregate aggregate = new DeliveryAggregate(delivery);
        DeliveryAggregate updatedAggregate = aggregate.updateLocation(location);

        Delivery savedDelivery = deliveryRepository.save(updatedAggregate.getDelivery());
        log.info("Location updated for delivery {}: {}", savedDelivery.getDeliveryNumber(), location);

        return DeliveryResponse.fromEntity(savedDelivery);
    }

    @Transactional
    public void deleteDelivery(UUID id) {
        if (!deliveryRepository.existsById(id)) {
            throw new RuntimeException("Delivery not found with id: " + id);
        }
        deliveryRepository.deleteById(id);
        log.info("Delivery deleted: {}", id);
    }

    @Transactional(readOnly = true)
    public List<DeliveryResponse> getOverdueDeliveries() {
        return deliveryRepository.findOverdueDeliveries(
                        DeliveryStatus.IN_TRANSIT,
                        LocalDateTime.now()
                ).stream()
                .map(DeliveryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getActiveDeliveriesCountByCourier(String courierId) {
        return deliveryRepository.countActiveDeliveriesByCourier(courierId);
    }

    @Transactional(readOnly = true)
    public Double getAverageDeliveryTime() {
        return deliveryRepository.getAverageDeliveryTime();
    }

    private String generateDeliveryNumber() {
        return "DEL-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 6);
    }
}
