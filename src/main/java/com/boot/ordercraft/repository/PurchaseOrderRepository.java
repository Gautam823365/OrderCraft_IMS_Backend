package com.boot.ordercraft.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.boot.ordercraft.model.PurchaseOrder;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

	@Query(value = "SELECT * FROM ordercraft_purchase_orders po "
			+ "WHERE (:orderDate IS NULL OR TRUNC(po.order_date) = TO_DATE(:orderDate, 'YYYY-MM-DD')) "
			+ "AND (:status IS NULL OR LOWER(TRIM(po.delivery_status)) = :status)", nativeQuery = true)
	List<PurchaseOrder> findByFilters(String orderDate, String status);
	
	@Query("SELECT po FROM PurchaseOrder po " +
		       "WHERE po.userId = :userId " +
		       "AND (:orderDate IS NULL OR CAST(po.orderDate AS string) = :orderDate) " +
		       "AND (:status IS NULL OR LOWER(po.deliveryStatus) = LOWER(:status))")
		List<PurchaseOrder> findByUserIdAndFilters(@Param("userId") Long userId,
		                                           @Param("orderDate") String orderDate,
		                                           @Param("status") String status);


}
