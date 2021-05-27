package de.uol.vpp.action.infrastructure.entities;

import de.uol.vpp.action.domain.enums.ManipulationTypeEnum;
import de.uol.vpp.action.infrastructure.entities.embedded.ManipulationPrimaryKey;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Datenbank-Entität der Erzeugungsmanipulation
 */
@Entity
@Data
public class ProducerManipulation {

    @EmbeddedId
    private ProducerManipulationPrimaryKey producerManipulationPrimaryKey;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ManipulationTypeEnum manipulationType;

    @Column(nullable = false)
    private Double capacity;

    @Embeddable
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProducerManipulationPrimaryKey implements Serializable {

        @Embedded
        private ManipulationPrimaryKey manipulationPrimaryKey;

        @Column(nullable = false)
        private String producerId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ProducerManipulationPrimaryKey that = (ProducerManipulationPrimaryKey) o;
            return Objects.equals(manipulationPrimaryKey.getActionRequest().getActionRequestId(), that.getManipulationPrimaryKey().getActionRequest().getActionRequestId()) &&
                    Objects.equals(manipulationPrimaryKey.getStartTimestamp(), that.getManipulationPrimaryKey().getStartTimestamp()) &&
                    Objects.equals(manipulationPrimaryKey.getEndTimestamp(), that.getManipulationPrimaryKey().getEndTimestamp()) &&
                    Objects.equals(producerId, that.producerId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(manipulationPrimaryKey.getActionRequest().getActionRequestId(), manipulationPrimaryKey.getStartTimestamp(), manipulationPrimaryKey.getEndTimestamp(), producerId);
        }
    }


}
