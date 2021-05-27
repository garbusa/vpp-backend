package de.uol.vpp.action.infrastructure.entities.embedded;

import de.uol.vpp.action.infrastructure.entities.ActionRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Primärschlüssel für Manipulationen-Entities
 */
@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ManipulationPrimaryKey implements Serializable {

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
        ManipulationPrimaryKey that = (ManipulationPrimaryKey) o;
        return Objects.equals(actionRequest.getActionRequestId(), that.actionRequest.getActionRequestId()) &&
                Objects.equals(startTimestamp, that.startTimestamp) &&
                Objects.equals(endTimestamp, that.endTimestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actionRequest.getActionRequestId(), startTimestamp, endTimestamp);
    }
}