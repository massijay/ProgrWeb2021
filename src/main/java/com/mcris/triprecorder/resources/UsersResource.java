package com.mcris.triprecorder.resources;

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

@Path("/users")
public class UsersResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getUsers(@Context ContainerRequest containerRequest){// https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-usagenotes-connect-drivermanager.html#connector-j-usagenotes-connect-drivermanager
        // https://www3.ntu.edu.sg/home/ehchua/programming/java/JDBC_Basic.html
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/trip_recorder?useSSL=false&serverTimezone=UTC",
                    "user", "user");

            String query = "select * from users";
            StringBuilder sb = new StringBuilder();
            String principal = containerRequest.getSecurityContext().getUserPrincipal().getName();
            sb.append(principal).append("\n\n");
            try(Statement smt = connection.createStatement()){
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
