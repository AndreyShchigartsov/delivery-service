package org.example.delivery.controller;

import lombok.RequiredArgsConstructor;
import org.example.delivery.dto.CourierAssignmentRequest;
import org.example.delivery.dto.DeliveryRequest;
import org.example.delivery.dto.DeliveryResponse;
import org.example.delivery.dto.DeliveryStatusUpdateRequest;
import org.example.delivery.service.DeliveryService;
import org.example.delivery.valueobject.DeliveryStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    // Create
    @PostMapping
    public ResponseEntity<DeliveryResponse> createDelivery(@RequestBody DeliveryRequest request) {
        return new ResponseEntity<>(deliveryService.createDelivery(request), HttpStatus.CREATED);
    }

    // Read - by id
    @GetMapping("/{id}")
    public ResponseEntity<DeliveryResponse> getDelivery(@PathVariable UUID id) {
        return ResponseEntity.ok(deliveryService.getDelivery(id));
    }

    // Read - by delivery number
    @GetMapping("/number/{deliveryNumber}")
    public ResponseEntity<DeliveryResponse> getDeliveryByNumber(@PathVariable String deliveryNumber) {
        return ResponseEntity.ok(deliveryService.getDeliveryByNumber(deliveryNumber));
    }

    // Read - by order id
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<DeliveryResponse>> getDeliveriesByOrder(@PathVariable String orderId) {
        return ResponseEntity.ok(deliveryService.getDeliveriesByOrder(orderId));
    }

    // Read - all
    @GetMapping
    public ResponseEntity<List<DeliveryResponse>> getAllDeliveries() {
        return ResponseEntity.ok(deliveryService.getAllDeliveries());
    }

    // Read - by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<DeliveryResponse>> getDeliveriesByStatus(@PathVariable DeliveryStatus status) {
        return ResponseEntity.ok(deliveryService.getDeliveriesByStatus(status));
    }

    // Read - by courier
    @GetMapping("/courier/{courierId}")
    public ResponseEntity<List<DeliveryResponse>> getDeliveriesByCourier(@PathVariable String courierId) {
        return ResponseEntity.ok(deliveryService.getDeliveriesByCourier(courierId));
    }

    // Read - overdue deliveries
    @GetMapping("/overdue")
    public ResponseEntity<List<DeliveryResponse>> getOverdueDeliveries() {
        return ResponseEntity.ok(deliveryService.getOverdueDeliveries());
    }

    // Read - statistics
    @GetMapping("/stats/avg-delivery-time")
    public ResponseEntity<Double> getAverageDeliveryTime() {
        return ResponseEntity.ok(deliveryService.getAverageDeliveryTime());
    }

    @GetMapping("/stats/courier/{courierId}/active")
    public ResponseEntity<Long> getActiveDeliveriesCountByCourier(@PathVariable String courierId) {
        return ResponseEntity.ok(deliveryService.getActiveDeliveriesCountByCourier(courierId));
    }

    // Update - assign courier
    @PostMapping("/{id}/assign-courier")
    public ResponseEntity<DeliveryResponse> assignCourier(@PathVariable UUID id,
                                                          @RequestBody CourierAssignmentRequest request) {
        return ResponseEntity.ok(deliveryService.assignCourier(id, request));
    }

    // Update - status
    @PatchMapping("/{id}/status")
    public ResponseEntity<DeliveryResponse> updateDeliveryStatus(@PathVariable UUID id,
                                                                 @RequestBody DeliveryStatusUpdateRequest request) {
        return ResponseEntity.ok(deliveryService.updateDeliveryStatus(id, request));
    }

    // Update - location
    @PatchMapping("/{id}/location")
    public ResponseEntity<DeliveryResponse> updateLocation(@PathVariable UUID id,
                                                           @RequestParam String location) {
        return ResponseEntity.ok(deliveryService.updateLocation(id, location));
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDelivery(@PathVariable UUID id) {
        deliveryService.deleteDelivery(id);
        return ResponseEntity.noContent().build();
    }
}
