package de.uol.vpp.action.infrastructure.entities;

import de.uol.vpp.action.domain.enums.GridManipulationTypeEnum;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Data
public class GridManipulation {

    @EmbeddedId
    private GridManipulationPrimaryKey gridManipulationPrimaryKey;


    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private GridManipulationTypeEnum manipulationType;

    @Column(nullable = false)
    private Double ratedPower;

    @Embeddable
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GridManipulationPrimaryKey implements Serializable {

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "action_request_id", referencedColumnName = "actionRequestId")
        private ActionRequest actionRequest;

        @Column(nullable = false)
        private ZonedDateTime startTimestamp;

        @Column(nullable = false)
        private ZonedDateTime endTimestamp;


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GridManipulationPrimaryKey that = (GridManipulationPrimaryKey) o;
            return Objects.equals(actionRequest.getActionRequestId(), that.actionRequest.getActionRequestId()) &&
                    Objects.equals(startTimestamp, that.startTimestamp) &&
                    Objects.equals(endTimestamp, that.endTimestamp);
        }

        @Override
        public int hashCode() {
            return Objects.hash(actionRequest.getActionRequestId(), startTimestamp, endTimestamp);
        }
    }


}
