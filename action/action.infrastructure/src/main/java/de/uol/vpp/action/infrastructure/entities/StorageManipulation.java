package de.uol.vpp.action.infrastructure.entities;

import de.uol.vpp.action.domain.enums.StorageManipulationEnum;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Data
public class StorageManipulation {

    @EmbeddedId
    private StorageManipulationPrimaryKey storageManipulationPrimaryKey;

    @Column
    private Double hours;

    @Column(nullable = false)
    private Double ratedPower;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private StorageManipulationEnum manipulationType;

    @Embeddable
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StorageManipulationPrimaryKey implements Serializable {

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "action_request_id", referencedColumnName = "actionRequestId")
        private ActionRequest actionRequest;

        @Column(nullable = false)
        private ZonedDateTime startTimestamp;

        @Column(nullable = false)
        private ZonedDateTime endTimestamp;

        @Column(nullable = false)
        private String storageId;


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StorageManipulationPrimaryKey that = (StorageManipulationPrimaryKey) o;
            return Objects.equals(actionRequest.getActionRequestId(), that.actionRequest.getActionRequestId()) &&
                    Objects.equals(startTimestamp, that.startTimestamp) &&
                    Objects.equals(endTimestamp, that.endTimestamp) &&
                    Objects.equals(storageId, that.storageId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(actionRequest.getActionRequestId(), startTimestamp, endTimestamp, storageId);
        }
    }


}
