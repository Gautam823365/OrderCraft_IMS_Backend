package com.boot.ordercraft.service;

import com.boot.ordercraft.model.PurchaseOrder;
import com.boot.ordercraft.model.PurchaseOrderItem;
import com.boot.ordercraft.repository.PurchaseOrderRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class DownloadPdfService {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

//    public ByteArrayInputStream generateAllOrdersPdf() {
//        Document document = new Document();
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//
//        try {
//            PdfWriter.getInstance(document, out);
//            document.open();
//
//            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
//            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
//            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
//
//            Paragraph title = new Paragraph("All Purchase Orders", titleFont);
//            title.setAlignment(Element.ALIGN_CENTER);
//            document.add(title);
//            document.add(Chunk.NEWLINE);
//
//            List<PurchaseOrder> orders = purchaseOrderRepository.findAll();
//            for (PurchaseOrder order : orders) {
//                PdfPTable table = new PdfPTable(2);
//                table.setWidthPercentage(100);
//                table.setSpacingBefore(10);
//
//                table.addCell(new PdfPCell(new Phrase("Order ID:", headFont)));
//                table.addCell(new PdfPCell(new Phrase(order.getPurchaseOrderId().toString(), normalFont)));
//
//                table.addCell(new PdfPCell(new Phrase("Order Date:", headFont)));
//                table.addCell(new PdfPCell(new Phrase(order.getOrderDate().toString(), normalFont)));
//
//                table.addCell(new PdfPCell(new Phrase("Expected Delivery:", headFont)));
//                table.addCell(new PdfPCell(new Phrase(order.getExpectedDelivery().toString(), normalFont)));
//
//                table.addCell(new PdfPCell(new Phrase("Status:", headFont)));
//                table.addCell(new PdfPCell(new Phrase(order.getDeliveryStatus(), normalFont)));
//
//                table.addCell(new PdfPCell(new Phrase("Supplier ID:", headFont)));
//                table.addCell(new PdfPCell(new Phrase(order.getSupplier().getSupplier_id().toString(), normalFont)));
//
//                document.add(table);
//
//                if (order.getItems() != null && !order.getItems().isEmpty()) {
//                    Paragraph itemsHeader = new Paragraph("Items Ordered:", headFont);
//                    document.add(itemsHeader);
//
//                    PdfPTable itemTable = new PdfPTable(3);
//                    itemTable.setWidthPercentage(100);
//                    itemTable.setSpacingBefore(5);
//
//                    itemTable.addCell("Product ID");
//                    itemTable.addCell("Product Name");
//                    itemTable.addCell("Quantity");
//
//                    for (PurchaseOrderItem item : order.getItems()) {
//                        itemTable.addCell(String.valueOf(item.getProduct().getProductId()));
//                        itemTable.addCell(item.getProduct().getProductName());
//                        itemTable.addCell(String.valueOf(item.getQuantity()));
//                    }
//
//                    document.add(itemTable);
//                }
//
//                document.add(Chunk.NEWLINE);
//            }
//
//            document.close();
//        } catch (DocumentException ex) {
//            ex.printStackTrace();
//        }
//
//        return new ByteArrayInputStream(out.toByteArray());
//    }
    
    public ByteArrayInputStream generateAllOrdersPdf() {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            Paragraph title = new Paragraph("All Purchase Orders", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            List<PurchaseOrder> orders = purchaseOrderRepository.findAll();
            for (PurchaseOrder order : orders) {
                PdfPTable table = new PdfPTable(2);
                table.setWidthPercentage(100);
                table.setSpacingBefore(10);

                table.addCell(new PdfPCell(new Phrase("Order ID:", headFont)));
                table.addCell(new PdfPCell(new Phrase(order.getPurchaseOrderId().toString(), normalFont)));

                table.addCell(new PdfPCell(new Phrase("Order Type:", headFont)));
                String orderTypeText = order.getOrdertype() != null
                        ? order.getOrdertype()
                        : "Order created way before order type was introduced.";
                table.addCell(new PdfPCell(new Phrase(orderTypeText, normalFont)));


                table.addCell(new PdfPCell(new Phrase("Order Date:", headFont)));
                table.addCell(new PdfPCell(new Phrase(order.getOrderDate().toString(), normalFont)));

                table.addCell(new PdfPCell(new Phrase("Expected Delivery:", headFont)));
                table.addCell(new PdfPCell(new Phrase(order.getExpectedDelivery().toString(), normalFont)));

                table.addCell(new PdfPCell(new Phrase("Status:", headFont)));
                table.addCell(new PdfPCell(new Phrase(order.getDeliveryStatus(), normalFont)));

                // Add Supplier Info
                table.addCell(new PdfPCell(new Phrase("Supplier Name:", headFont)));
                table.addCell(new PdfPCell(new Phrase(order.getSupplier().getSupplier_name(), normalFont)));

                table.addCell(new PdfPCell(new Phrase("Supplier Email:", headFont)));
                table.addCell(new PdfPCell(new Phrase(order.getSupplier().getContact_email(), normalFont)));

                table.addCell(new PdfPCell(new Phrase("Supplier Phone:", headFont)));
                table.addCell(new PdfPCell(new Phrase(order.getSupplier().getPhone(), normalFont)));

                // If order type is CUSTOMER, add customer info
                if ("CUSTOMER".equalsIgnoreCase(order.getOrdertype())) {
                    table.addCell(new PdfPCell(new Phrase("Customer Name:", headFont)));
                    table.addCell(new PdfPCell(new Phrase(order.getCustomer().getCust_name(), normalFont)));

                    table.addCell(new PdfPCell(new Phrase("Customer Email:", headFont)));
                    table.addCell(new PdfPCell(new Phrase(order.getCustomer().getCust_email(), normalFont)));

                    table.addCell(new PdfPCell(new Phrase("Customer Phone:", headFont)));
                    table.addCell(new PdfPCell(new Phrase(order.getCustomer().getCust_phoneno().toString(), normalFont)));

                    table.addCell(new PdfPCell(new Phrase("Customer Address:", headFont)));
                    table.addCell(new PdfPCell(new Phrase(
                            order.getCustomer().getStreet() + ", " +
                                    order.getCustomer().getCity() + ", " +
                                    order.getCustomer().getState() + " - " +
                                    order.getCustomer().getPostalcode() + ", " +
                                    order.getCustomer().getCountry(),
                            normalFont)));
                }

                document.add(table);

                // Add Items Table
                if (order.getItems() != null && !order.getItems().isEmpty()) {
                    Paragraph itemsHeader = new Paragraph("Items Ordered:", headFont);
                    itemsHeader.setSpacingBefore(5);
                    document.add(itemsHeader);

                    PdfPTable itemTable;

                    // Determine column count based on type
                    if ("CUSTOMER".equalsIgnoreCase(order.getOrdertype())) {
                        itemTable = new PdfPTable(4);
                        itemTable.addCell("Product ID");
                        itemTable.addCell("Product Name");
                        itemTable.addCell("Quantity");
                        itemTable.addCell("Cost");

                        for (PurchaseOrderItem item : order.getItems()) {
                            itemTable.addCell(String.valueOf(item.getProduct().getProductId()));
                            itemTable.addCell(item.getProduct().getProductName());
                            itemTable.addCell(String.valueOf(item.getQuantity()));
                            itemTable.addCell(String.valueOf(item.getCost()));
                        }
                    } else {
                        itemTable = new PdfPTable(4);
                        itemTable.addCell("Item Type");
                        itemTable.addCell("Name");
                        itemTable.addCell("Quantity");
                        itemTable.addCell("Cost");

                        for (PurchaseOrderItem item : order.getItems()) {
                            if (item.getProduct() != null) {
                                itemTable.addCell("Product");
                                itemTable.addCell(item.getProduct().getProductName());
                            } else if (item.getRawmaterial() != null) {
                                itemTable.addCell("Raw Material");
                                itemTable.addCell(item.getRawmaterial().getMaterial_name());
                            } else {
                                itemTable.addCell("Unknown");
                                itemTable.addCell("N/A");
                            }

                            itemTable.addCell(String.valueOf(item.getQuantity()));
                            itemTable.addCell(String.valueOf(item.getCost()));
                        }
                    }

                    itemTable.setWidthPercentage(100);
                    itemTable.setSpacingBefore(5);
                    document.add(itemTable);
                }

                document.add(Chunk.NEWLINE);
            }

            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

}
