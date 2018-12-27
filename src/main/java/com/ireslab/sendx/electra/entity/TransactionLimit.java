package com.ireslab.sendx.electra.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "transaction_limit")
@NamedQuery(name = "TransactionLimit.findAll", query = "SELECT c FROM TransactionLimit c")
public class TransactionLimit implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "transaction_limit_id")
	private Integer transactionLimitId;
	
	@Column(name = "daily_limit", nullable = true)
	private String dailyLimit;
	
	@Column(name = "transactions_per_day", nullable = true)
	private String transactionsPerDay;
	
	@Column(name = "monthly_limit", nullable = true)
	private String monthlyLimit;
	
	@Column(name = "modified_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;

	public Integer getTransactionLimitId() {
		return transactionLimitId;
	}

	public void setTransactionLimitId(Integer transactionLimitId) {
		this.transactionLimitId = transactionLimitId;
	}

	public String getDailyLimit() {
		return dailyLimit;
	}

	public void setDailyLimit(String dailyLimit) {
		this.dailyLimit = dailyLimit;
	}

	public String getTransactionsPerDay() {
		return transactionsPerDay;
	}

	public void setTransactionsPerDay(String transactionsPerDay) {
		this.transactionsPerDay = transactionsPerDay;
	}

	public String getMonthlyLimit() {
		return monthlyLimit;
	}

	public void setMonthlyLimit(String monthlyLimit) {
		this.monthlyLimit = monthlyLimit;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	@Override
	public String toString() {
		return "TransactionLimit [transactionLimitId=" + transactionLimitId + ", dailyLimit=" + dailyLimit
				+ ", transactionsPerDay=" + transactionsPerDay + ", monthlyLimit=" + monthlyLimit + ", modifiedDate="
				+ modifiedDate + "]";
	}
	
	
	
}
