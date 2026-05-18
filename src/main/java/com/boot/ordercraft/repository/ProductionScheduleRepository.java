package com.boot.ordercraft.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.boot.ordercraft.model.DemandedProductDTO;
import com.boot.ordercraft.model.Product;
import com.boot.ordercraft.model.ProductionSchedule;

public interface ProductionScheduleRepository extends JpaRepository<ProductionSchedule, Integer> {

//	@Query("SELECT new com.boot.ordercraft.model.DemandedProductDTO(" +
//		       "p.productId, p.productName, p.productUnitPrice, p.demandedQuantity, ps.psStatus) " +
//		       "FROM Product p " +
//		       "LEFT JOIN ProductionSchedule ps ON ps.psProductId = p " +
//		       "WHERE p.demandedQuantity > 0")
//		List<DemandedProductDTO> findDemandedProductsWithStatus();
	
//	@Query("""
//		    SELECT new com.boot.ordercraft.model.DemandedProductDTO(
//		        p.productId,
//		        p.productName,
//		        p.productUnitPrice,
//		        p.demandedQuantity,
//		        (
//		            SELECT ps2.psStatus
//		            FROM ProductionSchedule ps2
//		            WHERE ps2.psProductId = p
//		            AND ps2.psId = (
//		                SELECT MAX(ps3.psId)
//		                FROM ProductionSchedule ps3
//		                WHERE ps3.psProductId = p
//		            )
//		        )
//		    )
//		    FROM Product p
//		    WHERE p.demandedQuantity > 0
//		""")
//		List<DemandedProductDTO> findDemandedProductsWithStatus();

	@Query("""
		    SELECT new com.boot.ordercraft.model.DemandedProductDTO(
		        p.productId,
		        p.productName,
		        p.productUnitPrice,
		        p.demandedQuantity,
		        (
		            SELECT ps2.psStatus
		            FROM ProductionSchedule ps2
		            WHERE ps2.psProductId = p
		              AND ps2.psStatus <> 'DELIVERED'
		              AND ps2.psId = (
		                  SELECT MAX(ps3.psId)
		                  FROM ProductionSchedule ps3
		                  WHERE ps3.psProductId = p
		                    AND ps3.psStatus <> 'DELIVERED'
		              )
		        )
		    )
		    FROM Product p
		    WHERE p.demandedQuantity > 0
		""")
		List<DemandedProductDTO> findDemandedProductsWithStatus();

	
	 List<ProductionSchedule> findByPsEndDateBeforeAndPsStatusNot(LocalDate date, String status);
	    List<ProductionSchedule> findByPsStatus(String status);
	    List<ProductionSchedule> findByPsStartDateLessThanEqualAndPsStatus(LocalDate now, String status);
	    List<ProductionSchedule> findByPsEndDateLessThanEqualAndPsStatus(LocalDate now, String status);

	    List<ProductionSchedule> findByPsProductId_ProductId(Long productId);
	    
	    List<ProductionSchedule> findByPsDeadline(LocalDate deadline);

}
