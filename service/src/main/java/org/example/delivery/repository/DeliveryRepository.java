package org.example.delivery.repository;

import org.example.delivery.entity.Delivery;
import org.example.delivery.valueobject.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {

    Optional<Delivery> findByDeliveryNumber(String deliveryNumber);

    List<Delivery> findByOrderId(String orderId);

    List<Delivery> findByStatus(DeliveryStatus status);

    List<Delivery> findByCourierInfoCourierId(String courierId);

    @Query("SELECT d FROM Delivery d WHERE d.estimatedDeliveryTime BETWEEN :start AND :end")
    List<Delivery> findByEstimatedDeliveryTimeBetween(@Param("start") LocalDateTime start,
                                                      @Param("end") LocalDateTime end);

    @Query("SELECT d FROM Delivery d WHERE d.status = :status AND d.estimatedDeliveryTime < :now")
    List<Delivery> findOverdueDeliveries(@Param("status") DeliveryStatus status,
                                         @Param("now") LocalDateTime now);

    @Query("SELECT COUNT(d) FROM Delivery d WHERE d.courierInfo.courierId = :courierId AND d.status = 'IN_TRANSIT'")
    long countActiveDeliveriesByCourier(@Param("courierId") String courierId);

    @Query("SELECT AVG(EXTRACT(EPOCH FROM (d.actualDeliveryTime - d.pickupTime))) FROM Delivery d " +
            "WHERE d.status = 'DELIVERED' AND d.actualDeliveryTime IS NOT NULL")
    Double getAverageDeliveryTime();

    boolean existsByOrderIdAndStatusNot(String orderId, DeliveryStatus status);

    List<Delivery> findByDeliveryAddressCity(String city);
}
