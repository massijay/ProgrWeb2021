package com.mcris.triprecorder.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "geopoints", schema = "trip_recorder")
@NamedQueries({
        @NamedQuery(name = "Geopoint.getListByTripIdIfUser",
                query = "select t.geopoints from Trip t where t.id = :tripId and t.userId = :userId"),
        @NamedQuery(name = "Geopoint.deleteAllbyTripId",
                query = "delete from Geopoint g where g.tripId = :tripId"),
        @NamedQuery(name = "Geopoint.deletebyIdAndUserId",
                query = "delete from Geopoint g where g.id = :geopointId and g.trip.userId = :userId")
})

public class Geopoint {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "trip_id", nullable = false)
    private int tripId;
    @Basic
    @Column(name = "latitude")
    private double latitude;
    @Basic
    @Column(name = "longitude")
    private double longitude;
    @Basic
    @Column(name = "recorded_at")
    private Timestamp recordedAt;
    @Basic
    @Column(name = "label")
    private String label;
    @ManyToOne
    @JoinColumn(name = "trip_id", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnore
    private Trip trip;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Timestamp getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(Timestamp recordedAt) {
        this.recordedAt = recordedAt;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Geopoint geopoint = (Geopoint) o;
        return id == geopoint.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip tripsByTripId) {
        this.trip = tripsByTripId;
    }
}
