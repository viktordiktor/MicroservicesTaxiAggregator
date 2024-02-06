package com.nikonenko.rideservice.migrations;

import com.nikonenko.rideservice.models.RidePaymentMethod;
import com.nikonenko.rideservice.models.RideStatus;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.AllArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import java.time.LocalDateTime;

@AllArgsConstructor
@ChangeUnit(id = "1", order = "1", author = "viktordiktor")
public class CreateRidesCollectionChangeUnit {
    private final MongoTemplate mongoTemplate;

    @Execution
    public void changeSet() {
        Document rideDocument = new Document()
                .append("driverId", 1L)
                .append("passengerId", 1L)
                .append("startAddress", "123 Main St")
                .append("endAddress", "456 Elm St")
                .append("startDate", LocalDateTime.parse("2024-01-01T10:00:00"))
                .append("endDate", LocalDateTime.parse("2024-01-01T11:30:00"))
                .append("chargeId", "pi_3OgOzSEBrxpniCwT1WTRYChF")
                .append("distance", 10.5)
                .append("status", RideStatus.FINISHED.name())
                .append("ridePaymentMethod", RidePaymentMethod.BY_CARD.name());
        mongoTemplate.createCollection("rides")
                .insertOne(rideDocument);
    }

    @RollbackExecution
    public void rollback() {
        mongoTemplate.getCollection("rides").drop();
    }
}
