package de.uol.vpp.load.infrastructure.entities;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Data
public class ELoad {

    @EmbeddedId
    private ActionRequestTimestamp actionRequestTimestamp;

    @Column(nullable = false)
    private String virtualPowerPlantId;

    @OneToMany(mappedBy = "load", fetch = FetchType.EAGER)
    private List<ELoadHousehold> households;

    @Embeddable
    public static class ActionRequestTimestamp implements Serializable {
        private String actionRequestId;
        private ZonedDateTime timestamp;

        public ActionRequestTimestamp() {
            super();
        }

        public ActionRequestTimestamp(String actionRequestId, ZonedDateTime timestamp) {
            this.actionRequestId = actionRequestId;
            this.timestamp = timestamp;
        }

        public String getActionRequestId() {
            return actionRequestId;
        }


        public ZonedDateTime getTimestamp() {
            return timestamp;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ActionRequestTimestamp that = (ActionRequestTimestamp) o;
            return actionRequestId.equals(that.actionRequestId) &&
                    timestamp.equals(that.timestamp);
        }

        @Override
        public int hashCode() {
            return Objects.hash(actionRequestId, timestamp);
        }

        @Override
        public String toString() {
            return "ActionRequestTimestamp{" +
                    "actionRequestId='" + actionRequestId + '\'' +
                    ", timestamp=" + timestamp +
                    '}';
        }
    }

}
