package com.ireslab.sendx.electra.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "exchange")
@NamedQuery(name = "Exchange.findAll", query = "SELECT c FROM Exchange c")
public class Exchange {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "exchange_id", unique = true, nullable = false)
	private Integer exchangeId;

	@Column(name = "exchange_token", nullable = false, length = 50)
	private String exchangeToken;
	
	@Column(name = "native_currency", nullable = false, length = 50)
	private String nativeCurrency;
	
	@Column(name = "exchange_rate", nullable = false, length = 50)
	private String exchangeRate;
	
	@Column(name = "transfer_fee", nullable = false, length = 50)
	private String transferFee;
	
	@Column(name = "is_cryptocurrency", nullable = false)
	private boolean isCryptocurrency;
	
	@Column(name = "widthrawl_fee", nullable = false, length = 50)
	private String widthrawlFee;
	
	

	public boolean isCryptocurrency() {
		return isCryptocurrency;
	}

	public void setIsCryptocurrency(boolean isCryptocurrency) {
		this.isCryptocurrency = isCryptocurrency;
	}

	public Integer getExchangeId() {
		return exchangeId;
	}

	public String getExchangeToken() {
		return exchangeToken;
	}

	public String getNativeCurrency() {
		return nativeCurrency;
	}

	public String getExchangeRate() {
		return exchangeRate;
	}

	public String getTransferFee() {
		return transferFee;
	}

	public void setExchangeId(Integer exchangeId) {
		this.exchangeId = exchangeId;
	}

	public void setExchangeToken(String exchangeToken) {
		this.exchangeToken = exchangeToken;
	}

	public void setNativeCurrency(String nativeCurrency) {
		this.nativeCurrency = nativeCurrency;
	}

	public void setExchangeRate(String exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public void setTransferFee(String transferFee) {
		this.transferFee = transferFee;
	}

	/**
	 * @return the widthrawlFee
	 */
	public String getWidthrawlFee() {
		return widthrawlFee;
	}

	/**
	 * @param widthrawlFee the widthrawlFee to set
	 */
	public void setWidthrawlFee(String widthrawlFee) {
		this.widthrawlFee = widthrawlFee;
	}

	@Override
	public String toString() {
		return "Exchange [exchangeId=" + exchangeId + ", exchangeToken=" + exchangeToken + ", nativeCurrency="
				+ nativeCurrency + ", exchangeRate=" + exchangeRate + ", transferFee=" + transferFee
				+ ", isCryptocurrency=" + isCryptocurrency + "]";
	}
	
	
	
}
