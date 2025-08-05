package com.icbt.pahanaedubookshopjavaee.service.impl;

import com.icbt.pahanaedubookshopjavaee.service.BillGenerationService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;

public class BillGenerationServiceImpl implements BillGenerationService {
    
    private final DataSource dataSource;

    public BillGenerationServiceImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public byte[] generateBill(String orderCode) throws Exception {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Header
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("Pahana Edu Bookshop");
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(50, 730);
                contentStream.showText("Order Bill - " + orderCode);
                contentStream.endText();

                // Get order details from database
                String orderQuery = "SELECT o.order_code, o.customer_id, o.total_amount, o.total_discount_applied, " +
                                  "o.order_date, c.name as customer_name FROM Orders o " +
                                  "JOIN Customer c ON o.customer_id = c.account_number WHERE o.order_code = ?";
                
                String itemQuery = "SELECT oi.item_id, oi.qty, oi.unit_price, oi.line_total, oi.discount_applied, " +
                                 "i.name as item_name FROM Order_Item oi " +
                                 "JOIN Item i ON oi.item_id = i.item_code WHERE oi.order_id = ?";

                try (Connection conn = dataSource.getConnection()) {
                    // Order details
                    try (PreparedStatement ps = conn.prepareStatement(orderQuery)) {
                        ps.setString(1, orderCode);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                int yPosition = 700;
                                
                                contentStream.beginText();
                                contentStream.setFont(PDType1Font.HELVETICA, 10);
                                contentStream.newLineAtOffset(50, yPosition);
                                contentStream.showText("Customer: " + rs.getString("customer_name"));
                                contentStream.endText();

                                yPosition -= 20;
                                contentStream.beginText();
                                contentStream.newLineAtOffset(50, yPosition);
                                contentStream.showText("Date: " + rs.getTimestamp("order_date").toString());
                                contentStream.endText();

                                // Items header
                                yPosition -= 40;
                                contentStream.beginText();
                                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
                                contentStream.newLineAtOffset(50, yPosition);
                                contentStream.showText("Item Code");
                                contentStream.newLineAtOffset(80, 0);
                                contentStream.showText("Name");
                                contentStream.newLineAtOffset(120, 0);
                                contentStream.showText("Qty");
                                contentStream.newLineAtOffset(40, 0);
                                contentStream.showText("Price");
                                contentStream.newLineAtOffset(60, 0);
                                contentStream.showText("Discount%");
                                contentStream.newLineAtOffset(70, 0);
                                contentStream.showText("Total");
                                contentStream.endText();

                                // Items
                                try (PreparedStatement psItems = conn.prepareStatement(itemQuery)) {
                                    psItems.setString(1, orderCode);
                                    try (ResultSet rsItems = psItems.executeQuery()) {
                                        yPosition -= 20;
                                        while (rsItems.next()) {
                                            // Calculate discount percentage
                                            double unitPrice = rsItems.getBigDecimal("unit_price").doubleValue();
                                            double discountAmount = rsItems.getBigDecimal("discount_applied").doubleValue();
                                            double discountPercentage = (discountAmount / (unitPrice * rsItems.getInt("qty"))) * 100;
                                            
                                            contentStream.beginText();
                                            contentStream.setFont(PDType1Font.HELVETICA, 9);
                                            contentStream.newLineAtOffset(50, yPosition);
                                            contentStream.showText(rsItems.getString("item_id"));
                                            contentStream.newLineAtOffset(80, 0);
                                            contentStream.showText(rsItems.getString("item_name"));
                                            contentStream.newLineAtOffset(120, 0);
                                            contentStream.showText(String.valueOf(rsItems.getInt("qty")));
                                            contentStream.newLineAtOffset(40, 0);
                                            contentStream.showText(rsItems.getBigDecimal("unit_price").toString());
                                            contentStream.newLineAtOffset(60, 0);
                                            contentStream.showText(String.format("%.2f%%", discountPercentage));
                                            contentStream.newLineAtOffset(70, 0);
                                            contentStream.showText(rsItems.getBigDecimal("line_total").toString());
                                            contentStream.endText();
                                            yPosition -= 15;
                                        }
                                    }
                                }

                                // Total
                                yPosition -= 20;
                                contentStream.beginText();
                                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                                contentStream.newLineAtOffset(350, yPosition);
                                contentStream.showText("Total: Rs. " + rs.getBigDecimal("total_amount"));
                                contentStream.endText();
                            }
                        }
                    }
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
    }
}