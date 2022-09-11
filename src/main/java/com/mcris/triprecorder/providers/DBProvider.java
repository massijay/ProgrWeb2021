package com.mcris.triprecorder.providers;

import com.mcris.triprecorder.models.entities.Geopoint;
import com.mcris.triprecorder.models.entities.Session;
import com.mcris.triprecorder.models.entities.Trip;
import com.mcris.triprecorder.models.entities.User;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DBProvider {
    private static DBProvider instance = null;

    public static DBProvider getInstance() {
        if (instance == null) {
            instance = new DBProvider();
        }
        return instance;
    }

    private final EntityManagerFactory entityManagerFactory;

    private DBProvider() {
        entityManagerFactory = Persistence.createEntityManagerFactory("default");
    }

    private EntityManager getNewEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    public Session getSession(String sessionToken) {
        EntityManager em = getNewEntityManager();
        try {
            Session s = em.find(Session.class, UUID.fromString(sessionToken));
            Hibernate.initialize(s);
            return s;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            em.close();
        }
    }

    public Session createNewSession(User user) {
        if (user != null) {
            EntityManager em = getNewEntityManager();
            try {
                Session session = new Session();
                session.setUserId(user.getId());
                Instant instant = Instant.now().plusSeconds(1800);
                session.setExpireAt(Timestamp.from(instant));
                em.getTransaction().begin();
                em.persist(session);
                em.getTransaction().commit();
                return session;
            } catch (NoResultException ex) {
                return null;
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            } finally {
                em.close();
            }
        }
        return null;
    }

    public Session updateSession(Session session) {
        if (session != null) {
            EntityManager em = getNewEntityManager();
            EntityTransaction transaction = null;
            try {
                transaction = em.getTransaction();
                transaction.begin();
                Session merged = em.merge(session);
                transaction.commit();
                return merged;
            } catch (NoResultException ex) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }
                return null;
            } catch (Exception ex) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }
                throw new RuntimeException(ex);
            } finally {
                em.close();
            }
        }
        return null;
    }

    public boolean insertNewUser(User user) {
        if (user != null) {
            EntityManager em = getNewEntityManager();
            EntityTransaction transaction = null;
            try {
                transaction = em.getTransaction();
                transaction.begin();
                em.persist(user);
                transaction.commit();
                return true;
            } catch (NoResultException ex) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }
                return false;
            } catch (Exception ex) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }
                throw new RuntimeException(ex);
            } finally {
                em.close();
            }
        }
        return false;
    }

    public User updateUser(User user) {
        if (user != null) {
            EntityManager em = getNewEntityManager();
            EntityTransaction transaction = null;
            try {
                transaction = em.getTransaction();
                transaction.begin();
                User merged = em.merge(user);
                transaction.commit();
                return merged;
            } catch (NoResultException ex) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }
                return null;
            } catch (Exception ex) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }
                throw new RuntimeException(ex);
            } finally {
                em.close();
            }
        }
        return null;
    }

    public User getUserByUsername(String username) {
        EntityManager em = getNewEntityManager();
        try {
            TypedQuery<User> query = em.createNamedQuery("User.byUsername", User.class);
            query.setParameter("username", username);
            return query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            em.close();
        }
    }

    public User getUserByEmail(String email) {
        EntityManager em = getNewEntityManager();
        try {
            TypedQuery<User> query = em.createNamedQuery("User.byEmail", User.class);
            query.setParameter("email", email);
            return query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            em.close();
        }
    }

    public boolean deleteSession(UUID sessionToken) {
        if (sessionToken != null) {
            EntityManager em = getNewEntityManager();
            try {
                em.getTransaction().begin();
                Query query = em.createNamedQuery("Session.deleteByToken");
                query.setParameter("tokenUUID", sessionToken);
                int result = query.executeUpdate();
                em.getTransaction().commit();
                return result == 1;
            } catch (Exception ex) {
                return false;
            } finally {
                em.close();
            }
        }
        return false;
    }

    public void cleanupExpiredSessions() {
        EntityManager em = getNewEntityManager();
        EntityTransaction transaction = null;
        try {
            transaction = em.getTransaction();
            transaction.begin();
            Query query = em.createNamedQuery("Session.deleteExpired");
            query.executeUpdate();
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
        } finally {
            em.close();
        }
    }

    public List<Trip> getUserTrips(User user, LocalDate date) {
        EntityManager em = getNewEntityManager();
        try {
            List<Trip> trips;
            if (date != null) {
                Query query = em.createNamedQuery("Trip.getLatestByUserAndDate");
                query.setParameter("userId", user.getId());
                query.setParameter("tripDate", Date.valueOf(date), TemporalType.DATE);
                query.setParameter("nextDay", Date.valueOf(date.plusDays(1)), TemporalType.DATE);
                //noinspection unchecked
                trips = (List<Trip>) query.getResultList().stream()
                        .peek(trip -> ((Trip) trip).setGeopoints(null))
                        .collect(Collectors.toList());
            } else {
                Query query = em.createNamedQuery("Trip.getLatestByUser").setMaxResults(10);
                query.setParameter("userId", user.getId());
                //noinspection unchecked
                trips = (List<Trip>) query.getResultList().stream()
                        .peek(trip -> ((Trip) trip).setGeopoints(null))
                        .collect(Collectors.toList());
            }
            return trips;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            em.close();
        }
    }

    public Trip getUserTrip(int tripId, int userId) {
        if (tripId != 0 && userId != 0) {
            EntityManager em = getNewEntityManager();
            try {
                TypedQuery<Trip> query = em.createNamedQuery("Trip.getByIdIfCorrectUser", Trip.class);
                query.setParameter("tripId", tripId);
                query.setParameter("userId", userId);
                Trip trip = query.getSingleResult();
                Hibernate.initialize(trip.getGeopoints());
                return trip;
            } catch (NoResultException ex) {
                return null;
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            } finally {
                em.close();
            }
        }
        return null;
    }

    public Trip addOrUpdateTrip(Trip trip) {
        if (trip != null) {
            EntityManager em = getNewEntityManager();
            try {
                em.getTransaction().begin();
                Trip merged = em.merge(trip);
                em.getTransaction().commit();
                return merged;
            } catch (NoResultException ex) {
                return null;
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            } finally {
                em.close();
            }
        }
        return null;
    }

    public boolean deleteTripWithItsGeopoints(int tripId, int userId) {
        if (tripId != 0 && userId != 0) {
            EntityManager em = getNewEntityManager();
            EntityTransaction transaction = null;
            try {
                transaction = em.getTransaction();
                transaction.begin();
                Query query = em.createNamedQuery("Trip.removeByIdIfCorrectUser");
                query.setParameter("tripId", tripId);
                query.setParameter("userId", userId);
                int result = query.executeUpdate();
                if (result == 1) {
                    query = em.createNamedQuery("Geopoint.deleteAllbyTripId");
                    query.setParameter("tripId", tripId);
                    query.executeUpdate();
                    transaction.commit();
                    return true;
                }
                transaction.rollback();
                return false;
            } catch (Exception ex) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }
                return false;
            } finally {
                em.close();
            }
        }
        return false;
    }

    public List<Geopoint> getTripGeopoints(int tripId, int userId) {
        if (tripId != 0 && userId != 0) {
            EntityManager em = getNewEntityManager();
            try {
                Query query = em.createNamedQuery("Geopoint.getListByTripIdIfUser");
                query.setParameter("tripId", tripId);
                query.setParameter("userId", userId);
                //noinspection unchecked
                return (List<Geopoint>) query.getResultList();
            } catch (NoResultException ex) {
                return null;
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            } finally {
                em.close();
            }
        }
        return null;
    }

    public Geopoint addOrUpdateGeopoint(Geopoint geopoint) {
        if (geopoint != null) {
            EntityManager em = getNewEntityManager();
            try {
                em.getTransaction().begin();
                Geopoint merged = em.merge(geopoint);
                em.getTransaction().commit();
                return merged;
            } catch (NoResultException ex) {
                return null;
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            } finally {
                em.close();
            }
        }
        return null;
    }

    public List<Geopoint> addOrUpdateGeopoints(List<Geopoint> geopoints, int tripId) {
        if (geopoints != null) {
            List<Geopoint> updated = new ArrayList<>(geopoints.size());
            EntityManager em = getNewEntityManager();
            try {
                em.getTransaction().begin();
                for (Geopoint p : geopoints) {
                    p.setTripId(tripId);
                    updated.add(em.merge(p));
                }
                em.getTransaction().commit();
                return updated;
            } catch (NoResultException ex) {
                return null;
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            } finally {
                em.close();
            }
        }
        return null;
    }

    public boolean deleteGeopoint(int geopointId, int userId) {
        if (geopointId != 0 && userId != 0) {
            EntityManager em = getNewEntityManager();
            try {
                em.getTransaction().begin();
                Query query = em.createNamedQuery("Geopoint.deletebyIdAndUserId");
                query.setParameter("geopointId", geopointId);
                query.setParameter("userId", userId);
                int result = query.executeUpdate();
                em.getTransaction().commit();
                return result == 1;
            } catch (Exception ex) {
                return false;
            } finally {
                em.close();
            }
        }
        return false;
    }

    public boolean deleteGeopointsOfTrip(int tripId) {
        if (tripId != 0) {
            EntityManager em = getNewEntityManager();
            EntityTransaction transaction = null;
            try {
                transaction = em.getTransaction();
                transaction.begin();
                Query query = em.createNamedQuery("Geopoint.deleteAllbyTripId");
                query.setParameter("tripId", tripId);
                query.executeUpdate();
                em.getTransaction().commit();
                return true;
            } catch (Exception ex) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }
                return false;
            } finally {
                em.close();
            }
        }
        return false;
    }
}
