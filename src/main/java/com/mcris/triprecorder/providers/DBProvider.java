package com.mcris.triprecorder.providers;

import com.mcris.triprecorder.models.entities.Geopoint;
import com.mcris.triprecorder.models.entities.Session;
import com.mcris.triprecorder.models.entities.Trip;
import com.mcris.triprecorder.models.entities.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBProvider {
    // TODO: USE HIBERNATE
    private static DBProvider instance = null;

    public static Connection getConnection() throws SQLException {
        return getInstance().connection;
    }

    public static DBProvider getInstance() throws SQLException {
        if (instance == null) {
            // https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-usagenotes-connect-drivermanager.html#connector-j-usagenotes-connect-drivermanager
            // https://www3.ntu.edu.sg/home/ehchua/programming/java/JDBC_Basic.html
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            instance = new DBProvider();
            instance.init();
        }
        return instance;
    }

    private DBProvider() {
    }

    Connection connection = null;

    private void init() throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/trip_recorder?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                "user", "user");
    }

    public Session getSession(String sessionToken) {
        String sql = "select * " +
                "from sessions s inner join users u on s.user_id = u.id " +
                "where s.token = ?";
        try (PreparedStatement psmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            psmt.setString(1, sessionToken);
            ResultSet rset = psmt.executeQuery();
            if (rset.next()) {
                User user = new User();
                user.setId(rset.getInt("id"));
                user.setUsername(rset.getString("username"));
                user.setPassword(rset.getString("password"));
                user.setEmail(rset.getString("email"));
                user.setCreatedAt(rset.getTimestamp("created_at"));
                user.setUpdatedAt(rset.getTimestamp("updated_at"));
                Session session = new Session();
                session.setToken(rset.getString("token"));
                session.setUserId(rset.getInt("user_id"));
                session.setExpireAt(rset.getTimestamp("expire_at"));
                session.setUser(user);
                return session;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public List<Trip> getTripsofUser(User user) {
        ArrayList<Trip> trips = new ArrayList<>();
        String sql = "select t.* " +
                "from trips t inner join users u on t.user_id = u.id " +
                "where u.id = ?";
        try (PreparedStatement psmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            psmt.setInt(1, user.getId());
            ResultSet rset = psmt.executeQuery();
            while (rset.next()) {
                Trip trip = new Trip();
                trip.setId(rset.getInt("id"));
                trip.setUserId(user.getId());
                trip.setUser(user);
                trip.setName(rset.getString("trip_name"));
                trip.setDate(rset.getTimestamp("trip_date"));
                trip.setTransportType(rset.getString("transport_type"));
                trip.setNotes(rset.getString("notes"));
                trips.add(trip);
            }
            return trips;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Trip getTrip(int tripId){
        String sql = "select * " +
                "from trips t" +
                "where id = ?";
        try (PreparedStatement psmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            psmt.setInt(1, tripId);
            ResultSet rset = psmt.executeQuery();
            Trip trip = null;
            if(rset.next()){
                trip = new Trip();
                trip.setId(tripId);
                trip.setUserId(rset.getInt("user_id"));
                trip.setName(rset.getString("trip_name"));
                trip.setDate(rset.getTimestamp("trip_date"));
                trip.setTransportType(rset.getString("transport_type"));
                trip.setNotes(rset.getString("notes"));
            }
            return trip;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean insertGeopoints(List<Geopoint> geopoints) {
        return true;
    }

    public Trip insertTripAndGetUpdated(Trip trip) {
        String sql = "insert into trips (user_id, trip_name, trip_date, transport_type, notes) " +
                "values(?, ?, ?, ?, ?)";
        try (PreparedStatement psmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            psmt.setInt(1, trip.getUserId());
            psmt.setString(2, trip.getName());
            psmt.setTimestamp(3, trip.getDate());
            psmt.setString(4, trip.getTransportType());
            setStringOrNull(psmt, 5, trip.getNotes());
            psmt.executeUpdate();
            ResultSet resultSet = psmt.getGeneratedKeys();
            if (resultSet.next()) {
                trip.setId(resultSet.getInt(1));
            }
            return trip;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setStringOrNull(PreparedStatement preparedStatement, int parameterIndex, String string) throws SQLException {
        if (string != null) {
            preparedStatement.setString(parameterIndex, string);
        } else {
            preparedStatement.setNull(parameterIndex, Types.VARCHAR);
        }
    }

}
