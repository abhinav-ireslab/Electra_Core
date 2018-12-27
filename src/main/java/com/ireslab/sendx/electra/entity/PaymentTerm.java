/**
 * 
 */
package com.ireslab.sendx.electra.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * @author Sachin
 *
 */
@Entity
@Table(name = "payment_term")
@NamedQuery(name = "PaymentTerm.findAll", query = "SELECT p FROM PaymentTerm p")
public class PaymentTerm implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "payment_term_id", unique = true, nullable = false)
	private Integer paymentTermId;
	
	@Column(name = "payment_term", nullable = false)
	private String paymentTerm;
	
	@Column(name = "term_value", nullable = false)
	private Integer termValue;

	public Integer getPaymentTermId() {
		return paymentTermId;
	}

	public void setPaymentTermId(Integer paymentTermId) {
		this.paymentTermId = paymentTermId;
	}

	public String getPaymentTerm() {
		return paymentTerm;
	}

	public void setPaymentTerm(String paymentTerm) {
		this.paymentTerm = paymentTerm;
	}

	public Integer getTermValue() {
		return termValue;
	}

	public void setTermValue(Integer termValue) {
		this.termValue = termValue;
	}
	
	
}
