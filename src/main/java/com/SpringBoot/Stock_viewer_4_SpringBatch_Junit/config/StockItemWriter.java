package com.SpringBoot.Stock_viewer_4_SpringBatch_Junit.config;


import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.SpringBoot.Stock_viewer_4_SpringBatch_Junit.entity.Stock;
import com.SpringBoot.Stock_viewer_4_SpringBatch_Junit.repository.StockRepo;



@Component
public class StockItemWriter implements ItemWriter<Stock> {

    @Autowired
    private StockRepo Repo;


	@Override
	public void write(Chunk<? extends Stock > chunk) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Writer Thread "+Thread.currentThread().getName());
        Repo.saveAll(chunk);
	}
}
