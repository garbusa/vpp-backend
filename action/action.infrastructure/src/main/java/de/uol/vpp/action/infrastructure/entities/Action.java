package de.uol.vpp.action.infrastructure.entities;

import de.uol.vpp.action.domain.enums.ActionTypeEnum;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Data
public class Action {

    @EmbeddedId
    private ActionPrimaryKey actionPrimaryKey;

    @Column(nullable = false)
    private Boolean isStorage;

    @Column(nullable = false)
    private Double value;

    @Column(nullable = false)
    private Double hours;

    @Embeddable
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ActionPrimaryKey implements Serializable {

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumns({
                @JoinColumn(name = "action_request_id", referencedColumnName = "action_request_id"),
                @JoinColumn(name = "start_timestamp", referencedColumnName = "startTimestamp"),
                @JoinColumn(name = "end_timestamp", referencedColumnName = "endTimestamp")
        })
        private ActionCatalog actionCatalog;

        @Column(nullable = false)
        @Enumerated(EnumType.ORDINAL)
        private ActionTypeEnum actionType;

        @Column(nullable = false)
        private String producerOrStorageId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ActionPrimaryKey that = (ActionPrimaryKey) o;
            return Objects.equals(actionCatalog.getActionCatalogPrimaryKey().getActionRequest().getActionRequestId(), that.actionCatalog.getActionCatalogPrimaryKey().getActionRequest().getActionRequestId()) &&
                    Objects.equals(actionCatalog.getActionCatalogPrimaryKey().getStartTimestamp(), that.actionCatalog.getActionCatalogPrimaryKey().getStartTimestamp()) &&
                    Objects.equals(actionCatalog.getActionCatalogPrimaryKey().getEndTimestamp(), that.actionCatalog.getActionCatalogPrimaryKey().getEndTimestamp()) &&
                    actionType == that.actionType &&
                    Objects.equals(producerOrStorageId, that.producerOrStorageId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(actionCatalog.getActionCatalogPrimaryKey().getActionRequest().getActionRequestId(), actionCatalog.getActionCatalogPrimaryKey().getStartTimestamp(), actionCatalog.getActionCatalogPrimaryKey().getEndTimestamp(), actionType, producerOrStorageId);
        }
    }
}
