package de.uol.vpp.action.infrastructure.entities;

import de.uol.vpp.action.domain.enums.ManipulationTypeEnum;
import de.uol.vpp.action.infrastructure.entities.embedded.ManipulationPrimaryKey;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Datenbank-Entität der Speichermanipulation
 */
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
    private ManipulationTypeEnum manipulationType;


    @Embeddable
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StorageManipulationPrimaryKey implements Serializable {

        @Embedded
        private ManipulationPrimaryKey manipulationPrimaryKey;

        @Column(nullable = false)
        private String storageId;


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StorageManipulationPrimaryKey that = (StorageManipulationPrimaryKey) o;
            return Objects.equals(manipulationPrimaryKey.getActionRequest().getActionRequestId(), that.getManipulationPrimaryKey().getActionRequest().getActionRequestId()) &&
                    Objects.equals(manipulationPrimaryKey.getStartTimestamp(), that.getManipulationPrimaryKey().getStartTimestamp()) &&
                    Objects.equals(manipulationPrimaryKey.getEndTimestamp(), that.getManipulationPrimaryKey().getEndTimestamp()) &&
                    Objects.equals(storageId, that.storageId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(manipulationPrimaryKey.getActionRequest().getActionRequestId(), manipulationPrimaryKey.getStartTimestamp(), manipulationPrimaryKey.getEndTimestamp(), storageId);
        }
    }


}
