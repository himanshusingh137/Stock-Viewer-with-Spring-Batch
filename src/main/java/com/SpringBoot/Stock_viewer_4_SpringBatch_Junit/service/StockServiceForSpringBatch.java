package com.SpringBoot.Stock_viewer_4_SpringBatch_Junit.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.batch.core.Job;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.SpringBoot.Stock_viewer_4_SpringBatch_Junit.entity.Stock;
import com.SpringBoot.Stock_viewer_4_SpringBatch_Junit.repository.OhlcRepo;
import com.SpringBoot.Stock_viewer_4_SpringBatch_Junit.repository.StockRepo;

@Service
public class StockServiceForSpringBatch {

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private Job job;
	
	@Autowired
	JobExplorer jobExplorer;

	@Autowired
	private StockRepo stockRepo;

	@Autowired
	private OhlcRepo ohlcRepo;

	private final String TEMP_STORAGE = "C:/Users/HP/OneDrive/Desktop/temp_batch_spring/";

	@Async
	public CompletableFuture<FileUploadResponse> uploadCSV(MultipartFile file) {

		boolean processed = false;

		Logger logger = LoggerFactory.getLogger(StockServiceForSpringBatch.class);

		// file -> path we don't know
		// copy the file to some storage in your VM : get the file path
		// copy the file to DB : get the file path

		try {
			String originalFileName = file.getOriginalFilename();
			File fileToImport = new File(TEMP_STORAGE + originalFileName);
			file.transferTo(fileToImport);

			if (!isFileAlreadyProcessed(originalFileName)) {
				JobParameters jobParameters = new JobParametersBuilder()
						.addString("fullPathFileName", TEMP_STORAGE + originalFileName)
						.addLong("startAt", System.currentTimeMillis()).toJobParameters();

				JobExecution execution = jobLauncher.run(job, jobParameters);
				
				logger.info("saving csv : " + Thread.currentThread().getName());
				
			return	CompletableFuture.completedFuture(new FileUploadResponse(true, "File uploaded and processed successfully."));
				
			} else {
				logger.info("File already processed: " + originalFileName);
				return CompletableFuture.completedFuture(new FileUploadResponse(false, "File already uploaded."));
			}

		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException | IOException e) {
			e.printStackTrace();
			return CompletableFuture.completedFuture(new FileUploadResponse(false, "Error processing file."));
		}
		
		
	}
	
	 private boolean isFileAlreadyProcessed(String fileName) {
	        // Iterate through all job instances of the specific job
	        List<JobInstance> jobInstances = jobExplorer.getJobInstances(job.getName(), 0, Integer.MAX_VALUE);
	        for (JobInstance instance : jobInstances) {
	            List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(instance);
	            for (JobExecution execution : jobExecutions) {
	                String executedFileName = execution.getJobParameters().getString("fullPathFileName");
	                
	                if (executedFileName != null && executedFileName.endsWith(fileName) && execution.getStatus() == BatchStatus.COMPLETED) {
	                    return true; // Found a completed execution with the same file name
	                }
	            }
	        }
	        return false; // No completed execution found with the same file name
	    }

	public Stock getStockBySymbol(String symbol) {
		return stockRepo.findBySymbol(symbol);
	}

}