package com.mcris.triprecorder.models.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.UUID;

@Entity
@Table(name = "sessions", schema = "trip_recorder")
@NamedQuery(name = "Session.byToken", query = "select s from Session s where s.token = :tokenString")
public class Session {
    // https://www.codementor.io/@petrepopescu/how-to-use-string-uuid-in-hibernate-with-mysql-1jrhjh6ef5
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Id
    @Column(name = "token", updatable = false, nullable = false, columnDefinition = "VARCHAR(255)")
    @JdbcTypeCode(Types.VARCHAR)
    private UUID token;
    @Basic
    @Column(name = "user_id")
    private int userId;
    @Basic
    @Column(name = "expire_at")
    private Timestamp expireAt;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private User user;

    public UUID getToken() {
        return token;
    }

    public void setToken(UUID token) {
        this.token = token;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Timestamp getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(Timestamp expireAt) {
        this.expireAt = expireAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return userId == session.userId;
    }

    @Override
    public int hashCode() {
        return token != null ? token.hashCode() : 0;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
