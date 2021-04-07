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
    private VppTimestamp vppTimestamp;

    @Column(nullable = false)
    private boolean forecasted;

    @Column(nullable = false)
    private boolean outdated;

    @OneToMany(mappedBy = "load", fetch = FetchType.EAGER)
    private List<ELoadHousehold> households;

    @Embeddable
    public static class VppTimestamp implements Serializable {
        private String vppBusinessKey;
        private ZonedDateTime startTimestamp;

        public VppTimestamp() {
            super();
        }

        public VppTimestamp(String vppBusinessKey, ZonedDateTime startTimestamp) {
            this.vppBusinessKey = vppBusinessKey;
            this.startTimestamp = startTimestamp;
        }

        public String getVppBusinessKey() {
            return vppBusinessKey;
        }


        public ZonedDateTime getStartTimestamp() {
            return startTimestamp;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            VppTimestamp that = (VppTimestamp) o;
            return vppBusinessKey.equals(that.vppBusinessKey) &&
                    startTimestamp.equals(that.startTimestamp);
        }

        @Override
        public int hashCode() {
            return Objects.hash(vppBusinessKey, startTimestamp);
        }

        @Override
        public String toString() {
            return "VppTimestamp{" +
                    "vppBusinessKey='" + vppBusinessKey + '\'' +
                    ", startTimestamp=" + startTimestamp +
                    '}';
        }
    }

}
