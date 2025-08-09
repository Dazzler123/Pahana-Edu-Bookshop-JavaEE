package com.icbt.pahanaedubookshopjavaee.controller;

import com.icbt.pahanaedubookshopjavaee.service.BillGenerationService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/generate-bill")
public class GenerateBillServlet extends BaseServlet {

    private BillGenerationService billGenerationService;

    @Override
    protected void initializeServices() {
        this.billGenerationService = serviceFactory.createBillGenerationService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String orderCode = request.getParameter("orderCode");

        if (orderCode == null || orderCode.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Order code is required");
            return;
        }

        try {
            byte[] pdfBytes = billGenerationService.generateBill(orderCode);

            // Get customer account from order code for filename
            String filename = "bill_" + orderCode + ".pdf";

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            response.setContentLength(pdfBytes.length);

            response.getOutputStream().write(pdfBytes);
            response.getOutputStream().flush();

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Failed to generate bill: " + e.getMessage());
        }
    }
}