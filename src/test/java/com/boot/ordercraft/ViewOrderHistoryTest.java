package com.boot.ordercraft;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.boot.ordercraft.controller.PurchaseOrderController;
import com.boot.ordercraft.model.PurchaseOrder;
import com.boot.ordercraft.service.PurchaseOrderService;

//@WebMvcTest(PurchaseOrderController.class)

@WebMvcTest(controllers = PurchaseOrderController.class, excludeAutoConfiguration = {
		org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class })

public class ViewOrderHistoryTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PurchaseOrderService purchaseOrderService;

	private final LocalDate fixedDate = LocalDate.of(2024, 1, 1);

	// ------------------- STATUS ONLY ---------------------

	@Test
	public void testGetOrders_StatusPENDING() throws Exception {
		PurchaseOrder order = new PurchaseOrder();
		order.setPurchaseOrderId(101L);
		order.setDeliveryStatus("PENDING");

		Mockito.when(purchaseOrderService.getOrdersByFilters(null, "pending")).thenReturn(List.of(order));

		mockMvc.perform(get("/api/orders/history").param("status", "PENDING")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].deliveryStatus", is("PENDING")));
	}

	@Test
	public void testGetOrders_StatusDISPATCHED() throws Exception {
		PurchaseOrder order = new PurchaseOrder();
		order.setPurchaseOrderId(102L);
		order.setDeliveryStatus("DISPATCHED");

		Mockito.when(purchaseOrderService.getOrdersByFilters(null, "dispatched")).thenReturn(List.of(order));

		mockMvc.perform(get("/api/orders/history").param("status", "DISPATCHED")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].deliveryStatus", is("DISPATCHED")));
	}

	@Test
	public void testGetOrders_StatusDELIVERED() throws Exception {
		PurchaseOrder order = new PurchaseOrder();
		order.setPurchaseOrderId(103L);
		order.setDeliveryStatus("DELIVERED");

		Mockito.when(purchaseOrderService.getOrdersByFilters(null, "delivered")).thenReturn(List.of(order));

		mockMvc.perform(get("/api/orders/history").param("status", "DELIVERED")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].deliveryStatus", is("DELIVERED")));
	}

	// ------------------- DATE + STATUS ---------------------

	@Test
	public void testGetOrders_DateAndStatusPENDING() throws Exception {
		PurchaseOrder order = new PurchaseOrder();
		order.setPurchaseOrderId(111L);
		order.setDeliveryStatus("PENDING");

		Mockito.when(purchaseOrderService.getOrdersByFilters(fixedDate, "pending")).thenReturn(List.of(order));

		mockMvc.perform(get("/api/orders/history").param("date", "2024-01-01").param("status", "PENDING"))
				.andExpect(status().isOk()).andExpect(jsonPath("$[0].purchaseOrderId", is(111)));
	}

	@Test
	public void testGetOrders_DateAndStatusDISPATCHED() throws Exception {
		PurchaseOrder order = new PurchaseOrder();
		order.setPurchaseOrderId(112L);
		order.setDeliveryStatus("DISPATCHED");

		Mockito.when(purchaseOrderService.getOrdersByFilters(fixedDate, "dispatched")).thenReturn(List.of(order));

		mockMvc.perform(get("/api/orders/history").param("date", "2024-01-01").param("status", "DISPATCHED"))
				.andExpect(status().isOk()).andExpect(jsonPath("$[0].purchaseOrderId", is(112)));
	}

	@Test
	public void testGetOrders_DateAndStatusDELIVERED() throws Exception {
		PurchaseOrder order = new PurchaseOrder();
		order.setPurchaseOrderId(113L);
		order.setDeliveryStatus("DELIVERED");

		Mockito.when(purchaseOrderService.getOrdersByFilters(fixedDate, "delivered")).thenReturn(List.of(order));

		mockMvc.perform(get("/api/orders/history").param("date", "2024-01-01").param("status", "DELIVERED"))
				.andExpect(status().isOk()).andExpect(jsonPath("$[0].purchaseOrderId", is(113)));
	}

	// ------------------- INVALID DATE ---------------------

	@Test
	public void testGetOrders_InvalidDateFormat() throws Exception {
		mockMvc.perform(get("/api/orders/history").param("date", "invalid-date").param("status", "PENDING"))
				.andExpect(status().isBadRequest());
	}

	// ------------------- SERVICE ERROR ---------------------

	@Test
	public void testGetOrders_ServiceThrowsException() throws Exception {
		Mockito.when(purchaseOrderService.getOrdersByFilters(any(), any()))
				.thenThrow(new RuntimeException("Something went wrong"));

		mockMvc.perform(get("/api/orders/history").param("date", "2024-01-01").param("status", "PENDING"))
				.andExpect(status().isInternalServerError());
	}
}
