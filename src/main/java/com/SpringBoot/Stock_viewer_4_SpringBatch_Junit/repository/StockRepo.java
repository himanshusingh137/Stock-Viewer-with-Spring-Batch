package com.SpringBoot.Stock_viewer_4_SpringBatch_Junit.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.SpringBoot.Stock_viewer_4_SpringBatch_Junit.entity.Stock;

@Repository
public interface StockRepo extends JpaRepository<Stock, Long>{

	public Stock findBySymbol(String symbol);
	
}
