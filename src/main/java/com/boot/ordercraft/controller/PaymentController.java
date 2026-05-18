//package com.boot.ordercraft.controller;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.client.RestTemplate;
//
//import com.boot.ordercraft.model.PurchaseOrder;
//
//@RestController
//@RequestMapping("/api/payment")
////@CrossOrigin(origins = "http://localhost:53898")
//@CrossOrigin(origins = {"http://localhost:53898", "http://localhost:56160"})
//public class PaymentController {
//
//    @Value("${cashfree.client.id}")
//    private String clientId;
//
//    @Value("${cashfree.client.secret}")
//    private String clientSecret;
//
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    @PostMapping("/initiate-payment")
//    public ResponseEntity<Map<String, String>> initiatePayment(@RequestBody PurchaseOrder purchaseOrder) {
//        try {
//            // Create order payload for Cashfree
//            Map<String, Object> requestBody = new HashMap<>();
//            requestBody.put("order_amount", calculateTotal(purchaseOrder.getItems()));
//            requestBody.put("order_currency", "INR");
//            requestBody.put("customer_details", Map.of(
//                "customer_id", "CUST001",
//                "customer_email", "test@example.com",
//                "customer_phone", "9999999999"
//            ));
//            requestBody.put("order_id", UUID.randomUUID().toString()); // Unique order ID
//            requestBody.put("order_note", "Purchase Order Payment");
//            requestBody.put("return_url", "http://localhost:4200/payment-success?order_id={order_id}");
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            headers.set("x-client-id", "TEST10745621c816a39b1285012654701");
//            headers.set("x-client-secret", "cfsk_ma_test_b8ac78bf6e5097480fe28d_036dc74b");
//
//            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
//            RestTemplate restTemplate = new RestTemplate();
//            ResponseEntity<Map> response = restTemplate.postForEntity(
//                "https://sandbox.cashfree.com/pg/orders", entity, Map.class
//            );
//
//            Map<String, Object> responseBody = response.getBody();
//            String paymentLink = (String) ((Map<String, Object>) responseBody.get("payment_session")).get("redirect_url");
//
//            // Temporarily save the order in DB or session (optional)
//            // Or store in Redis/DB with `order_id` as key
//
//            Map<String, String> result = new HashMap<>();
//            result.put("paymentLink", paymentLink);
//            return ResponseEntity.ok(result);
//
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("error", "Payment session creation failed"));
//        }
//    }
//
//
//}
