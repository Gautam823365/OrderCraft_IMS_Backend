package com.boot.ordercraft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import com.boot.ordercraft.model.InventoryTransactions;

@Repository
//public interface InventoryTransactionsRepository extends JpaRepository<InventoryTransactions, Integer> {
//}

public interface InventoryTransactionsRepository 
extends JpaRepository<InventoryTransactions, Integer>, JpaSpecificationExecutor<InventoryTransactions> {
}
