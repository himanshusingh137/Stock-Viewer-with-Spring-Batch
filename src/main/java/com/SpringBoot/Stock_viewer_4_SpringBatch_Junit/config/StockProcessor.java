package com.SpringBoot.Stock_viewer_4_SpringBatch_Junit.config;

import java.util.ArrayList;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import com.SpringBoot.Stock_viewer_4_SpringBatch_Junit.entity.OHLC;
import com.SpringBoot.Stock_viewer_4_SpringBatch_Junit.entity.Stock;
import com.SpringBoot.Stock_viewer_4_SpringBatch_Junit.repository.StockRepo;



public class StockProcessor implements ItemProcessor<Stock, Stock> {
//    @Override
//    public Stock process(Stock stock) {
//        if (stock.getSeries().equals("EQ")) {
//            return stock; // Return the stock object as it is
//        } else {
//            return null; // Filter out the record if series is not "EQ"
//        }
//    }

	@Autowired
	private StockRepo stockRepo;

	@Override
	public Stock process(Stock stock) throws Exception {
	    // Check if the series is "EQ"
	    if (!"EQ".equals(stock.getSeries())) {
	        // Filter out non-"EQ" series
	        return null;
	    }

	    // Check if the symbol exists in the database
	    Stock existingStock = stockRepo.findBySymbol(stock.getSymbol());

	    if (existingStock != null) {
	        // Symbol exists
	        if (existingStock.getOhlc() == null) {
	            // Initialize OHLC list if it's null
	            existingStock.setOhlc(new ArrayList<>());
	        }
	        for (OHLC ohlc : stock.getOhlc()) {
	        	// Check if an OHLC with the same timestamp exists
	            boolean exists = existingStock.getOhlc().stream()
	                    .anyMatch(existingOhlc -> existingOhlc.getTimestamp().equals(ohlc.getTimestamp()));
	            if (!exists) {
	                ohlc.setStock(existingStock); // Ensure the back-reference is set
	                existingStock.getOhlc().add(ohlc);
	            }
	        }
	        stockRepo.save(existingStock); // Save the updated stock entity
	        return existingStock; // Return the updated stock object
	    } else {
	        // Symbol doesn't exist, save the new stock entity
	        stockRepo.save(stock);
	        return stock;
	    }
	}
}