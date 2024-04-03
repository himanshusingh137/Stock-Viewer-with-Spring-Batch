package com.SpringBoot.Stock_viewer_4_SpringBatch_Junit.controller;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.SpringBoot.Stock_viewer_4_SpringBatch_Junit.repository.StockRepo;
import com.SpringBoot.Stock_viewer_4_SpringBatch_Junit.service.FileUploadResponse;
import com.SpringBoot.Stock_viewer_4_SpringBatch_Junit.service.StockServiceForSpringBatch;

@Controller
public class BatchJobController {

	
	@Autowired
	private StockServiceForSpringBatch serviceForSpringBatch;

	@Autowired
	private StockRepo stockRepo;


	

	@PostMapping("/upload-csv")
	public ResponseEntity<Map<String, Object>> uploadCSV(@RequestParam("file") MultipartFile file) {

		long startTime = System.currentTimeMillis();
		
	
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			if (file.isEmpty()) {
				System.out.println("file is empty");
				response.put("success", false);
				response.put("message", "Please select a file to upload.");
				return ResponseEntity.badRequest().body(response);
			}
			System.out.println("file is not empty");


			CompletableFuture<FileUploadResponse> uploadResult = serviceForSpringBatch.uploadCSV(file);
			
			// Wait for the CompletableFuture to complete
            FileUploadResponse fileUploadResponse = uploadResult.join(); // This will block until the CompletableFuture completes
          
			System.out.println("uploadcsv complete");
			
			if (fileUploadResponse.getFlag()) {
				System.out.println("succed");
				response.put("success", true);
				response.put("message", fileUploadResponse.getMessage());
			} else {
				System.out.println("fail to upload");
				response.put("success", false);
				response.put("message", fileUploadResponse.getMessage());
			}

			long endTime = System.currentTimeMillis();
			long elapsedTime = endTime - startTime;
			System.out.println("upload csv endpoint Execution time: " + elapsedTime + "ms");

			return ResponseEntity.ok().body(response);

		} catch (Exception e) {
			System.out.println("problem occured");
			response.put("success", false);
			response.put("message", "An error occurred while uploading the file.");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}

	}
	
	@RequestMapping("/home")
	public String home() {
		long startTime = System.currentTimeMillis();
		long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
		System.out.println("home endpoint Execution time: " + elapsedTime + "ms");
		return "Upload";
	}
	

}
