package de.uol.vpp.action.infrastructure.entities;

import de.uol.vpp.action.domain.enums.ProblemTypeEnum;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
public class ActionCatalog {

    @EmbeddedId
    private ActionCatalogPrimaryKey actionCatalogPrimaryKey;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ProblemTypeEnum problemType;

    @Column(nullable = false)
    private Double cumulativeGap;

    @OneToMany(mappedBy = "actionPrimaryKey.actionCatalog", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private Set<Action> actions;


    @Embeddable
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ActionCatalogPrimaryKey implements Serializable {

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
            ActionCatalogPrimaryKey that = (ActionCatalogPrimaryKey) o;
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
