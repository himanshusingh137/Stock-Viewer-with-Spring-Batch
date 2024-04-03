package com.SpringBoot.Stock_viewer_4_SpringBatch_Junit.entity;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class OHLC {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long ohlc_id;

	private double open;

	private double high;

	private double low;

	private double close;

	private double last;

	private double prevclose;

	@Temporal(TemporalType.DATE)
	private Date timestamp;

	private String isin;

	@ManyToOne(cascade =CascadeType.PERSIST )
	@JoinColumn(name = "stock_id")
	private Stock stock;

	public OHLC(long ohlc_id, double open, double high, double low, double close, double last, double prevclose,
			Date timestamp, String isin, Stock stock) {
		super();
		this.ohlc_id = ohlc_id;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.last = last;
		this.prevclose = prevclose;
		this.timestamp = timestamp;
		this.isin = isin;
		this.stock = stock;
	}
	
	

}
