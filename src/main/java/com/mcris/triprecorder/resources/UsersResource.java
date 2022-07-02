package com.mcris.triprecorder.resources;

import com.mcris.triprecorder.models.entities.Trip;
import com.mcris.triprecorder.models.entities.User;
import com.mcris.triprecorder.providers.DBProvider;
import org.glassfish.jersey.server.ContainerRequest;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import java.sql.*;
import java.util.List;

@Path("/users")
public class UsersResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getUsers(@Context ContainerRequest containerRequest){
        // https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-usagenotes-connect-drivermanager.html#connector-j-usagenotes-connect-drivermanager
        // https://www3.ntu.edu.sg/home/ehchua/programming/java/JDBC_Basic.html
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            String query = "select * from users";
            StringBuilder sb = new StringBuilder();
            User user = (User) containerRequest.getSecurityContext().getUserPrincipal();
            List<Trip> trips = DBProvider.getInstance().getTripsofUser(user);
            sb.append(user.getUsername()).append("\n\n");
            try(Statement smt = DBProvider.getConnection().createStatement()){
                ResultSet rset = smt.executeQuery(query);
                while (rset.next()){
                    sb.append(rset.getInt("id"))
                            .append('\t')
                            .append(rset.getString("username"))
                            .append('\t')
                            .append(rset.getString("password"))
                            .append('\t')
                            .append(rset.getString("email"))
                            .append('\n');
                }
            }
            return sb.toString();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
