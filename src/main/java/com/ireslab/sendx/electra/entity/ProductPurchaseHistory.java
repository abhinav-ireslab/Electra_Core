/**
 * 
 */
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

/**
 * @author ireslab
 *
 */
/**
 * The persistent class for the product_purchase_history database table.
 * 
 */
@Entity
@Table(name = "product_purchase_history")
@NamedQuery(name = "ProductPurchaseHistory.findAll", query = "SELECT pph FROM ProductPurchaseHistory pph")
public class ProductPurchaseHistory implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "purchase_id", unique = true, nullable = false)
	private Integer purchaseId;
	
	@Column(name = "product_id", nullable = false)
	private Integer productId;
	
	@Column(name = "qty", nullable = false)
	private Double qty;
	
	@Column(name = "total_price", nullable = false)
	private Double totalPrice;
	
	@Column(name = "purchase_date", nullable = false)
	private Date purchaseDate;
	
	@Column(name = "purchaser_id", nullable = false)
	private Integer purchaserId;
	
	@Column(name = "invoice_number", nullable = false)
	private String invoiceNumber;
	
	@Column(name = "is_client", nullable = false)
	private boolean isClient;
	
	public ProductPurchaseHistory() {
		
	}

	public Integer getPurchaseId() {
		return purchaseId;
	}

	public void setPurchaseId(Integer purchaseId) {
		this.purchaseId = purchaseId;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Double getQty() {
		return qty;
	}

	public void setQty(Double qty) {
		this.qty = qty;
	}

	public Double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public Date getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public Integer getPurchaserId() {
		return purchaserId;
	}

	public void setPurchaserId(Integer purchaserId) {
		this.purchaserId = purchaserId;
	}

	public boolean isClient() {
		return isClient;
	}

	public void setClient(boolean isClient) {
		this.isClient = isClient;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	
	
	
}
