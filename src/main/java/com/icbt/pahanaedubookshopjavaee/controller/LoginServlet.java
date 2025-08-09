package com.icbt.pahanaedubookshopjavaee.controller;

import com.icbt.pahanaedubookshopjavaee.service.AuthService;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends BaseStatelessServlet {

    private AuthService authService;

    @Override
    protected void initializeServices() {
        this.authService = serviceFactory.createAuthService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try (JsonReader reader = Json.createReader(request.getReader())) {
            JsonObject json = reader.readObject();

            String username = json.getString("username", "");
            String password = json.getString("password", "");

            if (authService.authenticate(username, password)) {
                HttpSession session = request.getSession();
                session.setAttribute("username", username);
                session.setAttribute("isLoggedIn", true);

                JsonObject responseJson = Json.createObjectBuilder()
                        .add("success", true)
                        .add("message", "Login successful")
                        .add("username", username)
                        .build();

                abstractResponseUtility.writeJson(response, responseJson);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                JsonObject responseJson = Json.createObjectBuilder()
                        .add("success", false)
                        .add("message", "Invalid username or password")
                        .build();

                abstractResponseUtility.writeJson(response, responseJson);
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject responseJson = Json.createObjectBuilder()
                    .add("success", false)
                    .add("message", "Login failed: " + e.getMessage())
                    .build();

            abstractResponseUtility.writeJson(response, responseJson);
        }
    }
}