package de.uol.vpp.load.infrastructure.entities;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Data
public class ELoadHousehold {

    @EmbeddedId
    private HouseholdTimestamp householdTimestamp;

    @Column(nullable = false)
    private Double loadValue;

    @Column(nullable = false)
    private Integer householdMemberAmount;

    @ManyToOne()
    @JoinColumns({
            @JoinColumn(name = "eload_vpp_business_key", referencedColumnName = "vppBusinessKey"),
            @JoinColumn(name = "eload_start_timestamp", referencedColumnName = "startTimestamp")
    })
    private ELoad load;

    @Embeddable
    public static class HouseholdTimestamp implements Serializable {
        private String householdBusinessKey;
        private ZonedDateTime startTimestamp;

        public HouseholdTimestamp() {
            super();
        }

        public HouseholdTimestamp(String householdBusinessKey, ZonedDateTime startTimestamp) {
            this.householdBusinessKey = householdBusinessKey;
            this.startTimestamp = startTimestamp;
        }

        public String getHouseholdBusinessKey() {
            return householdBusinessKey;
        }

        public void setHouseholdBusinessKey(String householdBusinessKey) {
            this.householdBusinessKey = householdBusinessKey;
        }

        public ZonedDateTime getStartTimestamp() {
            return startTimestamp;
        }

        public void setStartTimestamp(ZonedDateTime startTimestamp) {
            this.startTimestamp = startTimestamp;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            HouseholdTimestamp that = (HouseholdTimestamp) o;
            return householdBusinessKey.equals(that.householdBusinessKey) &&
                    startTimestamp.equals(that.startTimestamp);
        }

        @Override
        public int hashCode() {
            return Objects.hash(householdBusinessKey, startTimestamp);
        }

        @Override
        public String toString() {
            return "HouseholdTimestamp{" +
                    "householdTimestamp='" + householdBusinessKey + '\'' +
                    ", startTimestamp=" + startTimestamp +
                    '}';
        }
    }

}
