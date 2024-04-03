package com.SpringBoot.Stock_viewer_4_SpringBatch_Junit.config;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.batch.core.Job;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.SpringBoot.Stock_viewer_4_SpringBatch_Junit.entity.OHLC;
import com.SpringBoot.Stock_viewer_4_SpringBatch_Junit.entity.Stock;
import com.SpringBoot.Stock_viewer_4_SpringBatch_Junit.repository.StockRepo;


@Configuration
public class BatchConfig {
	
//    @Autowired
//    private MyAsyncConfig myAsyncConfig;

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private PlatformTransactionManager platformTransactionManager;

	@Autowired
	private StockRepo stockRepo;

	@Autowired
	private StockItemWriter stockItemWriter;


	// Define CSV reader, processor, and writer beans here

	@Bean
	@StepScope
	public FlatFileItemReader<Stock> itemReader(@Value("#{jobParameters[fullPathFileName]}") String pathToFile) {

		System.out.println("enter reader");

		FlatFileItemReader<Stock> flatFileItemReader = new FlatFileItemReader<>();
		flatFileItemReader.setResource(new FileSystemResource(new File(pathToFile)));
		flatFileItemReader.setName("CSV-Reader");
		flatFileItemReader.setLinesToSkip(1);

		// Set LineMapper with custom FieldSetMapper
		DefaultLineMapper<Stock> lineMapper = new DefaultLineMapper<>();

		lineMapper.setLineTokenizer(new DelimitedLineTokenizer() {
			{
				setDelimiter(",");
				setStrict(false);
				setNames(new String[] { "SYMBOL", "SERIES", "OPEN", "HIGH", "LOW", "CLOSE", "LAST", "PREVCLOSE",
						"TOTTRDQTY", "TOTTRDVAL", "TIMESTAMP", "TOTALTRADES", "ISIN" });
			}
		});
		lineMapper.setFieldSetMapper(dynamicFieldSetMapper()); // Set the custom FieldSetMapper

		flatFileItemReader.setLineMapper(lineMapper);

		return flatFileItemReader;
	}

	private FieldSetMapper<Stock> dynamicFieldSetMapper() {
		return fieldSet -> {
			Stock stock = new Stock();
			OHLC ohlc = new OHLC();
			stock.setSymbol(fieldSet.readString("SYMBOL"));
			stock.setSeries(fieldSet.readString("SERIES"));
			ohlc.setOpen(fieldSet.readDouble("OPEN"));
			ohlc.setHigh(fieldSet.readDouble("HIGH"));
			ohlc.setLow(fieldSet.readDouble("LOW"));
			ohlc.setClose(fieldSet.readDouble("CLOSE"));
			ohlc.setLast(fieldSet.readDouble("LAST"));
			ohlc.setPrevclose(fieldSet.readDouble("PREVCLOSE"));
            ohlc.setIsin(fieldSet.readString("ISIN"));
			// Parse the timestamp string into a Date object
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
			Date timestamp = null;
			try {
				timestamp = dateFormat.parse(fieldSet.readString("TIMESTAMP"));
			} catch (ParseException e) {
				e.printStackTrace();
				// Handle parsing exception
			}
			ohlc.setTimestamp(timestamp);
			ohlc.setStock(stock);
			
			if (stock.getOhlc() == null) {
			    stock.setOhlc(new ArrayList<>());
			}
			ohlc.setStock(stock);
			stock.getOhlc().add(ohlc);

			return stock;
		};
	}

	@Bean
	public StockProcessor processor() {
		return new StockProcessor();
	}

	@Bean
	public RepositoryItemWriter<Stock> writer() {
		RepositoryItemWriter<Stock> writer = new RepositoryItemWriter<>();
		writer.setRepository(stockRepo);
		writer.setMethodName("save");
		return writer;
	}

	@Bean
	public Step step1(FlatFileItemReader<Stock> itemReader) {
		return new StepBuilder("step", jobRepository).<Stock, Stock>chunk(100, platformTransactionManager)
				.reader(itemReader)
				.processor(processor())
				.writer(stockItemWriter)
				.taskExecutor(taskExecutor())   //myAsyncConfig.myTaskExecutor()
				.build();
	}

	@Bean
	public Job runJob(FlatFileItemReader<Stock> itemReader) {
		return new JobBuilder("job", jobRepository).start(step1(itemReader)).build();
	}

	@Bean
	public TaskExecutor taskExecutor() {
		SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
		taskExecutor.setConcurrencyLimit(10);
		return taskExecutor;
	}

}