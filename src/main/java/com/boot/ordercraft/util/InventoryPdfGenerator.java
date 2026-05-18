package com.boot.ordercraft.util;

import com.boot.ordercraft.model.InventoryTransactions;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import jakarta.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.List;

public class InventoryPdfGenerator {

    public static void generatePdf(List<InventoryTransactions> transactions, HttpServletResponse response) {
        try {
            // ✅ Calculate Summary Stats
            double totalIn = transactions.stream()
                    .filter(tx -> tx.getTransactionType().equalsIgnoreCase("IN") 
                               || tx.getTransactionType().equalsIgnoreCase("ADD") 
                               || tx.getTransactionType().equalsIgnoreCase("DELIVER"))
                    .mapToDouble(InventoryTransactions::getQuantity)
                    .sum();

            double totalOut = transactions.stream()
                    .filter(tx -> tx.getTransactionType().equalsIgnoreCase("OUT") 
                               || tx.getTransactionType().equalsIgnoreCase("CONSUME"))
                    .mapToDouble(InventoryTransactions::getQuantity)
                    .sum();

            double stockBalance = totalIn - totalOut;
            double turnoverRate = (totalOut == 0) ? 0 : (totalOut / ((totalIn + stockBalance) / 2.0)) * 100;

            // ✅ Build HTML
            StringBuilder html = new StringBuilder();
            html.append("<html><head><style>")
                .append("body { font-family: Arial, sans-serif; font-size: 12px; }")
                .append("h2 { text-align: center; margin-bottom: 20px; }")
                .append(".summary { display: flex; justify-content: space-between; margin-bottom: 20px; }")
                .append(".card { flex: 1; margin: 0 8px; padding: 10px; border-radius: 8px; background: #f8f8f8; text-align: center; box-shadow: 0 2px 4px rgba(0,0,0,0.1);} ")
                .append(".card h4 { margin: 5px 0; font-size: 14px; color: #333; }")
                .append(".card p { font-size: 16px; font-weight: bold; color: #2c3e50; }")
                .append("table {width: 100%; border-collapse: collapse; margin-bottom: 15px;}")
                .append("th, td {border: 1px solid #ddd; padding: 8px; text-align: left;}")
                .append("th {background-color: #f4f4f4;}")
                .append("</style></head><body>");

            html.append("<h2>Inventory Transactions Detailed Report</h2>");

            // ✅ Summary Dashboard
            html.append("<div class='summary'>")
                .append("<div class='card'><h4>Total IN</h4><p>").append(totalIn).append("</p></div>")
                .append("<div class='card'><h4>Total OUT</h4><p>").append(totalOut).append("</p></div>")
                .append("<div class='card'><h4>Stock Balance</h4><p>").append(stockBalance).append("</p></div>")
                .append("<div class='card'><h4>Turnover Rate</h4><p>").append(String.format("%.2f", turnoverRate)).append(" %</p></div>")
                .append("</div>");

            // ✅ Detailed Transactions
            for (InventoryTransactions tx : transactions) {
                html.append("<div class='section'>");
                html.append("<h3>📑 Transaction Info</h3>")
                    .append("<table>")
                    .append("<tr><th>ID</th><td>").append(tx.getTransactionId()).append("</td></tr>")
                    .append("<tr><th>Date</th><td>").append(tx.getTransactionDate()).append("</td></tr>")
                    .append("<tr><th>Type</th><td>").append(tx.getTransactionType()).append("</td></tr>")
                    .append("<tr><th>Quantity</th><td>").append(tx.getQuantity()).append("</td></tr>")
                    .append("<tr><th>Reference</th><td>").append(tx.getReference() != null ? tx.getReference() : "—").append("</td></tr>")
                    .append("</table>");

                if (tx.getProduct() != null) {
                    html.append("<h3>📦 Product Info</h3>")
                        .append("<table>")
                        .append("<tr><th>Name</th><td>").append(tx.getProduct().getProductName()).append("</td></tr>")
                        .append("<tr><th>Description</th><td>").append(tx.getProduct().getProductDescription()).append("</td></tr>")
                        .append("<tr><th>Category</th><td>").append(tx.getProduct().getCategory() != null ? tx.getProduct().getCategory().getCategoryName() : "—").append("</td></tr>")
                        .append("<tr><th>Price</th><td>").append(tx.getProduct().getProductUnitPrice()).append("</td></tr>")
                        .append("<tr><th>Available Qty</th><td>").append(tx.getProduct().getProductQuantity()).append("</td></tr>")
                        .append("</table>");
                }

                if (tx.getUserId() != null) {
                    html.append("<h3>👤 Performed By</h3>")
                        .append("<table>")
                        .append("<tr><th>User</th><td>").append(tx.getUserId().getUsername()).append("</td></tr>")
                        .append("<tr><th>Email</th><td>").append(tx.getUserId().getEmail()).append("</td></tr>")
                        .append("<tr><th>Role</th><td>").append(tx.getUserId().getRole() != null ? tx.getUserId().getRole().getRoleName() : "—").append("</td></tr>")
                        .append("<tr><th>Phone</th><td>").append(tx.getUserId().getPhoneno()).append("</td></tr>")
                        .append("</table>");
                }

                html.append("</div><hr/>");
            }

            html.append("</body></html>");

            // ✅ Render PDF
            try (OutputStream out = response.getOutputStream()) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.useFastMode();
                builder.withHtmlContent(html.toString(), null);
                builder.toStream(out);
                builder.run();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }
}

