package com.icbt.pahanaedubookshopjavaee.controller;

import com.icbt.pahanaedubookshopjavaee.util.AbstractResponseUtility;

import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    private AbstractResponseUtility responseUtility;

    @Override
    public void init() {
        this.responseUtility = new AbstractResponseUtility();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        JsonObject responseJson = Json.createObjectBuilder()
                .add("success", true)
                .add("message", "Logged out successfully")
                .build();

        responseUtility.writeJson(response, responseJson);
    }
}