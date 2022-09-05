package com.mcris.triprecorder.providers;

import com.mcris.triprecorder.models.entities.Geopoint;
import com.mcris.triprecorder.models.entities.Session;
import com.mcris.triprecorder.models.entities.Trip;
import com.mcris.triprecorder.models.entities.User;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

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

    private EntityManager getNewNetityManager() {
        return entityManagerFactory.createEntityManager();
    }

    public Session getSession(String sessionToken) {
        EntityManager em = getNewNetityManager();
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
            EntityManager em = getNewNetityManager();
            try {
                Session session = new Session();
                session.setUserId(user.getId());
                session.setExpireAt(Timestamp.valueOf("2030-04-04 23:59:59"));
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

    public boolean deleteSession(UUID sessionToken) {
        if (sessionToken != null) {
            EntityManager em = getNewNetityManager();
            try {
                em.getTransaction().begin();
                Query query = em.createNamedQuery("Session.deleteByToken");
                query.setParameter("tokenString", sessionToken);
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

    public User getUserbyUsernameAndPassword(String username, String password) {
        EntityManager em = getNewNetityManager();
        try {
            TypedQuery<User> query = em.createNamedQuery("User.byUsernameAndPassword", User.class);
            query.setParameter("username", username);
            query.setParameter("password", password);
            return query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            em.close();
        }
    }

    public Collection<Trip> getUserTrips(User user) {
        EntityManager em = getNewNetityManager();
        try {
            Collection<Trip> trips = em.find(User.class, user.getId()).getTrips();
            Hibernate.initialize(trips);
            return trips;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            em.close();
        }
    }

    public Trip getUserTrip(int tripId, int userId) {
        if (tripId != 0 && userId != 0) {
            EntityManager em = getNewNetityManager();
            try {
                TypedQuery<Trip> query = em.createNamedQuery("Trip.getByIdIfCorrectUser", Trip.class);
                query.setParameter("tripId", tripId);
                query.setParameter("userId", userId);
                return query.getSingleResult();
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
            EntityManager em = getNewNetityManager();
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

    public boolean deleteTrip(int tripId, int userId) {
        if (tripId != 0 && userId != 0) {
            EntityManager em = getNewNetityManager();
            try {
                em.getTransaction().begin();
                Query query = em.createNamedQuery("Trip.removeByIdIfCorrectUser");
                query.setParameter("tripId", tripId);
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

    public List<Geopoint> getTripGeopoints(int tripId, int userId) {
        if (tripId != 0 && userId != 0) {
            EntityManager em = getNewNetityManager();
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
            EntityManager em = getNewNetityManager();
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
}
