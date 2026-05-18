package com.boot.ordercraft.service;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.stereotype.Component;

import com.boot.ordercraft.model.PurchaseOrder;
import com.boot.ordercraft.model.PurchaseOrderItem;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Component
public class InvoiceGenerator {

    public byte[] generateInvoice(PurchaseOrder order, boolean paymentSuccess) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document doc = new Document();
            PdfWriter.getInstance(doc, out); // ✅ Correct usage in iText 5

            doc.open();

            doc.add(new Paragraph("Invoice for Order"));
            doc.add(new Paragraph("Order Type: " + order.getOrdertype()));
            doc.add(new Paragraph("Payment Status: " + (paymentSuccess ? "Success" : "Failed")));
            doc.add(new Paragraph("Order Date: " + order.getOrderDate()));
            doc.add(new Paragraph("Expected Delivery: " + order.getExpectedDelivery()));

            PdfPTable table = new PdfPTable(3);
            table.addCell("Item");
            table.addCell("Quantity");
            table.addCell("Cost");

            for (PurchaseOrderItem item : order.getItems()) {
                String itemName = order.getOrdertype().equalsIgnoreCase("CUSTOMER")
                        ? item.getProduct().getProductName()
                        : item.getRawmaterial().getMaterial_name();

                table.addCell(itemName);
                table.addCell(item.getQuantity().toString());
                table.addCell(item.getCost().toString());
            }

            doc.add(table);
            doc.close();

            return out.toByteArray();
        } catch (DocumentException e) {
            e.printStackTrace();
            return null;
        }
    }
}
