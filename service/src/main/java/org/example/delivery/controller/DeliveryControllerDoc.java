package org.example.delivery.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.delivery.dto.CourierAssignmentRequest;
import org.example.delivery.dto.DeliveryRequest;
import org.example.delivery.dto.DeliveryResponse;
import org.example.delivery.dto.DeliveryStatusUpdateRequest;
import org.example.delivery.valueobject.DeliveryStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Tag(name = "Delivery Controller", description = "API для управления доставками")
public interface DeliveryControllerDoc {

    @Operation(
            summary = "Создать новую доставку",
            description = "Создает новую доставку для заказа. Статус доставки устанавливается в PENDING."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Доставка успешно создана",
                    content = @Content(schema = @Schema(implementation = DeliveryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Неверный запрос"),
            @ApiResponse(responseCode = "409", description = "Доставка для этого заказа уже существует")
    })
    ResponseEntity<DeliveryResponse> createDelivery(
            @Parameter(description = "Данные для создания доставки", required = true)
            @RequestBody(description = "Запрос на создание доставки", required = true)
            DeliveryRequest request
    );

    @Operation(
            summary = "Получить доставку по ID",
            description = "Возвращает информацию о доставке по её UUID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Доставка найдена",
                    content = @Content(schema = @Schema(implementation = DeliveryResponse.class))),
            @ApiResponse(responseCode = "404", description = "Доставка не найдена")
    })
    ResponseEntity<DeliveryResponse> getDelivery(
            @Parameter(description = "UUID доставки", required = true,
                    example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id
    );

    @Operation(
            summary = "Получить доставку по номеру",
            description = "Возвращает информацию о доставке по её уникальному номеру (формат: DEL-XXXXXXXXX-XXXXXX)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Доставка найдена",
                    content = @Content(schema = @Schema(implementation = DeliveryResponse.class))),
            @ApiResponse(responseCode = "404", description = "Доставка не найдена")
    })
    ResponseEntity<DeliveryResponse> getDeliveryByNumber(
            @Parameter(description = "Уникальный номер доставки", required = true,
                    example = "DEL-1734567890123-abc123")
            @PathVariable String deliveryNumber
    );

    @Operation(
            summary = "Получить все доставки по заказу",
            description = "Возвращает список всех доставок для указанного заказа"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список доставок получен",
                    content = @Content(schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "404", description = "Заказ не найден")
    })
    ResponseEntity<List<DeliveryResponse>> getDeliveriesByOrder(
            @Parameter(description = "ID заказа", required = true, example = "ORD-123456")
            @PathVariable String orderId
    );

    @Operation(
            summary = "Получить все доставки",
            description = "Возвращает список всех доставок в системе"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список доставок получен",
                    content = @Content(schema = @Schema(implementation = List.class)))
    })
    ResponseEntity<List<DeliveryResponse>> getAllDeliveries();

    @Operation(
            summary = "Получить доставки по статусу",
            description = "Возвращает список доставок с указанным статусом"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список доставок получен",
                    content = @Content(schema = @Schema(implementation = List.class)))
    })
    ResponseEntity<List<DeliveryResponse>> getDeliveriesByStatus(
            @Parameter(description = "Статус доставки", required = true,
                    example = "PENDING", schema = @Schema(implementation = DeliveryStatus.class))
            @PathVariable DeliveryStatus status
    );

    @Operation(
            summary = "Получить доставки по курьеру",
            description = "Возвращает список доставок, назначенных на указанного курьера"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список доставок получен",
                    content = @Content(schema = @Schema(implementation = List.class)))
    })
    ResponseEntity<List<DeliveryResponse>> getDeliveriesByCourier(
            @Parameter(description = "ID курьера", required = true, example = "COURIER-001")
            @PathVariable String courierId
    );

    @Operation(
            summary = "Получить просроченные доставки",
            description = "Возвращает список доставок, которые находятся в статусе IN_TRANSIT и превысили ожидаемое время доставки"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список просроченных доставок получен",
                    content = @Content(schema = @Schema(implementation = List.class)))
    })
    ResponseEntity<List<DeliveryResponse>> getOverdueDeliveries();

    @Operation(
            summary = "Получить среднее время доставки",
            description = "Возвращает среднее время доставки (в секундах) по всем завершенным доставкам"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Среднее время получено",
                    content = @Content(schema = @Schema(implementation = Double.class)))
    })
    ResponseEntity<Double> getAverageDeliveryTime();

    @Operation(
            summary = "Получить количество активных доставок курьера",
            description = "Возвращает количество активных доставок (IN_TRANSIT) для указанного курьера"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Количество получено",
                    content = @Content(schema = @Schema(implementation = Long.class)))
    })
    ResponseEntity<Long> getActiveDeliveriesCountByCourier(
            @Parameter(description = "ID курьера", required = true, example = "COURIER-001")
            @PathVariable String courierId
    );

    @Operation(
            summary = "Назначить курьера на доставку",
            description = "Назначает курьера для выполнения доставки. Статус доставки меняется на ASSIGNED."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Курьер успешно назначен",
                    content = @Content(schema = @Schema(implementation = DeliveryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Невозможно назначить курьера (неверный статус)"),
            @ApiResponse(responseCode = "404", description = "Доставка не найдена")
    })
    ResponseEntity<DeliveryResponse> assignCourier(
            @Parameter(description = "UUID доставки", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Данные о курьере", required = true)
            @RequestBody(description = "Запрос на назначение курьера", required = true)
            CourierAssignmentRequest request
    );

    @Operation(
            summary = "Обновить статус доставки",
            description = "Изменяет статус доставки. Поддерживаемые статусы: PICKED_UP, IN_TRANSIT, OUT_FOR_DELIVERY, DELIVERED, ATTEMPTED, CANCELLED, RETURNED"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статус успешно обновлен",
                    content = @Content(schema = @Schema(implementation = DeliveryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Недопустимый статус или переход"),
            @ApiResponse(responseCode = "404", description = "Доставка не найдена")
    })
    ResponseEntity<DeliveryResponse> updateDeliveryStatus(
            @Parameter(description = "UUID доставки", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Новый статус доставки", required = true)
            @RequestBody(description = "Запрос на обновление статуса", required = true)
            DeliveryStatusUpdateRequest request
    );

    @Operation(
            summary = "Обновить местоположение доставки",
            description = "Обновляет текущее местоположение доставки (для отслеживания)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Местоположение успешно обновлено",
                    content = @Content(schema = @Schema(implementation = DeliveryResponse.class))),
            @ApiResponse(responseCode = "404", description = "Доставка не найдена")
    })
    ResponseEntity<DeliveryResponse> updateLocation(
            @Parameter(description = "UUID доставки", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Новое местоположение", required = true, example = "г. Москва, ул. Тверская, 10")
            @RequestParam String location
    );

    @Operation(
            summary = "Удалить доставку",
            description = "Удаляет доставку по её UUID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Доставка успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Доставка не найдена")
    })
    ResponseEntity<Void> deleteDelivery(
            @Parameter(description = "UUID доставки", required = true)
            @PathVariable UUID id
    );
}
