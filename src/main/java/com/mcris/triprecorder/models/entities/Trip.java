package com.mcris.triprecorder.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Collection;

@Entity
@Table(name = "trips", schema = "trip_recorder")
public class Trip {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "user_id", insertable = false, updatable = false)
    private int userId;
    @Basic
    @Column(name = "trip_name")
    private String name;
    @Basic
    @Column(name = "trip_date")
    private Timestamp date;
    @Basic
    @Column(name = "transport_type")
    private String transportType;
    @Basic
    @Column(name = "notes")
    private String notes;
    @OneToMany(mappedBy = "trip")
    private Collection<Geopoint> geopoints;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String tripName) {
        this.name = tripName;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trip trip = (Trip) o;
        return id == trip.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public Collection<Geopoint> getGeopoints() {
        return geopoints;
    }

    public void setGeopoints(Collection<Geopoint> geopointsById) {
        this.geopoints = geopointsById;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User usersByUserId) {
        this.user = usersByUserId;
    }
}
