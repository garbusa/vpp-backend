package de.uol.vpp.action.infrastructure.entities;

import de.uol.vpp.action.domain.enums.ProducerManipulationTypeEnum;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Data
public class ProducerManipulation {

    @EmbeddedId
    private ProducerManipulationPrimaryKey producerManipulationPrimaryKey;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ProducerManipulationTypeEnum manipulationType;

    @Column(nullable = false)
    private Double capacity;

    @Embeddable
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProducerManipulationPrimaryKey implements Serializable {

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "action_request_id", referencedColumnName = "actionRequestId")
        private ActionRequest actionRequest;

        @Column(nullable = false)
        private ZonedDateTime startTimestamp;

        @Column(nullable = false)
        private ZonedDateTime endTimestamp;

        @Column(nullable = false)
        private String producerId;


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ProducerManipulationPrimaryKey that = (ProducerManipulationPrimaryKey) o;
            return Objects.equals(actionRequest.getActionRequestId(), that.actionRequest.getActionRequestId()) &&
                    Objects.equals(startTimestamp, that.startTimestamp) &&
                    Objects.equals(endTimestamp, that.endTimestamp) &&
                    Objects.equals(producerId, that.producerId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(actionRequest.getActionRequestId(), startTimestamp, endTimestamp, producerId);
        }
    }


}
