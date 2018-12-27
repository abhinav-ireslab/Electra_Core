/**
 * 
 */
package com.ireslab.sendx.electra.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.ireslab.sendx.electra.dto.Gst;
import com.ireslab.sendx.electra.dto.Total;

/**
 * @author ireslab
 *
 */
/**
 * The persistent class for the product database table.
 * 
 */
@Entity
@Table(name = "product")
@NamedQuery(name = "Product.findAll", query = "SELECT p FROM Product p")
public class Product implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "product_id", unique = true, nullable = false)
	private Integer productId;
	
	@Column(name = "client_id", nullable = false)
	private Integer clientId;
	
	@Column(name = "product_code", unique = true, nullable = false)
	private String productCode;
	
	@Column(name = "product_name", nullable = true)
	private String productName;
	
	@Column(name = "product_description", nullable = true)
	private String productDescription;
	
	@Column(name = "product_sku", nullable = true)
	private String productSku;
	
	@Column(name = "rate", nullable = false)
	private Double rate;
	
	@Column(name = "tax", nullable = true)
	private Double tax;

	@Column(name = "qty", nullable = true)
	private Double qty;
	
	@Column(name = "unit", nullable = true)
	private String unit;
	
	@Column(name = "unit_range", nullable = true)
	private String unitRange;
	
	@Column(name = "product_image_url", nullable = true)
	private String productImageUrl;
	
	@Column(name = "product_image_name", nullable = true)
	private String productImageName;
	
	@Column(name = "manufacturing_date", nullable = true)
	private Date manufacturingDate;
	
	@Column(name = "expiry_date", nullable = true)
	private Date expiryDate;
	
	@Column(name = "created_date", nullable = false)
	private Date createdDate;
	
	@Column(name = "modified_date", nullable = false)
	private Timestamp modifiedDate;
	
	@Column(name = "audit_logon_id", nullable = true)
	private String auditLogonId;

	@Column(name = "product_group", nullable = true)
	private String productGroup;
	
	// --------Producat Configuration -------------
	@Column(name = "item_code", nullable = true)
	private String itemCode;
	
	@Column(name = "invoice_type", nullable = true)
	private String invoiceType;
	
	@Column(name = "gst_inclusive")
	private boolean gstInclusive;
	
	@Column(name = "payment_terms", nullable = true)
	private String paymentTerms;
	
	@Column(name = "item_name_or_desc", nullable = true)
	private String itemNameOrDesc;
	
	@Column(name = "item_type_or_chapter", nullable = true)
	private String itemTypeOrChapter;
	
	@Column(name = "discount_percentage", nullable = true)
	private String discountPercentage;
	
	@Column(name = "item_price", nullable = true)
	private String itemPrice;
	
	@Column(name = "available_quantity", nullable = true)
	private String availableQuantity;
	
		//--- total---
	@Column(name = "sub_total", nullable = true)
	private String subTotal;
	
	@Column(name = "discount", nullable = true)
	private String discount;
	
	@Column(name = "total_tax_inclusive", nullable = true)
	private String totalTaxInclusive;
	
	@Column(name = "total", nullable = true)
	private String total;
		//-------------
	
		//--- gst---
	@Column(name = "cgst", nullable = true)
	private String cgst;
	@Column(name = "sgst_utgst", nullable = true)
	private String sgst_utgst;
	@Column(name = "igst", nullable = true)
	private String igst;
		//-------------
	@Column(name = "customer_notes", nullable = true)
	private String customerNotes;
	
	@Column(name = "terms_and_conditions", nullable = true)
	private String termsAndConditions;
	
	//---------------------------------------------
	
	@Column(name = "inter_state")
	private boolean interState;
	
	
	
	
	public boolean isInterState() {
		return interState;
	}

	public void setInterState(boolean interState) {
		this.interState = interState;
	}

	public Product() {
		
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}

	public boolean isGstInclusive() {
		return gstInclusive;
	}

	public void setGstInclusive(boolean gstInclusive) {
		this.gstInclusive = gstInclusive;
	}

	public String getPaymentTerms() {
		return paymentTerms;
	}

	public void setPaymentTerms(String paymentTerms) {
		this.paymentTerms = paymentTerms;
	}

	public String getItemNameOrDesc() {
		return itemNameOrDesc;
	}

	public void setItemNameOrDesc(String itemNameOrDesc) {
		this.itemNameOrDesc = itemNameOrDesc;
	}

	public String getItemTypeOrChapter() {
		return itemTypeOrChapter;
	}

	public void setItemTypeOrChapter(String itemTypeOrChapter) {
		this.itemTypeOrChapter = itemTypeOrChapter;
	}

	public String getDiscountPercentage() {
		return discountPercentage;
	}

	public void setDiscountPercentage(String discountPercentage) {
		this.discountPercentage = discountPercentage;
	}

	public String getItemPrice() {
		return itemPrice;
	}

	public void setItemPrice(String itemPrice) {
		this.itemPrice = itemPrice;
	}

	public String getAvailableQuantity() {
		return availableQuantity;
	}

	public void setAvailableQuantity(String availableQuantity) {
		this.availableQuantity = availableQuantity;
	}

	public String getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(String subTotal) {
		this.subTotal = subTotal;
	}

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

	public String getTotalTaxInclusive() {
		return totalTaxInclusive;
	}

	public void setTotalTaxInclusive(String totalTaxInclusive) {
		this.totalTaxInclusive = totalTaxInclusive;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getCgst() {
		return cgst;
	}

	public void setCgst(String cgst) {
		this.cgst = cgst;
	}

	public String getSgst_utgst() {
		return sgst_utgst;
	}

	public void setSgst_utgst(String sgst_utgst) {
		this.sgst_utgst = sgst_utgst;
	}

	public String getIgst() {
		return igst;
	}

	public void setIgst(String igst) {
		this.igst = igst;
	}

	public String getCustomerNotes() {
		return customerNotes;
	}

	public void setCustomerNotes(String customerNotes) {
		this.customerNotes = customerNotes;
	}

	public String getTermsAndConditions() {
		return termsAndConditions;
	}

	public void setTermsAndConditions(String termsAndConditions) {
		this.termsAndConditions = termsAndConditions;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Integer getClientId() {
		return clientId;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public String getProductSku() {
		return productSku;
	}

	public void setProductSku(String productSku) {
		this.productSku = productSku;
	}

	public Double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}

	public Double getQty() {
		return qty;
	}

	public void setQty(Double qty) {
		this.qty = qty;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getUnitRange() {
		return unitRange;
	}

	public void setUnitRange(String unitRange) {
		this.unitRange = unitRange;
	}

	public String getProductImageUrl() {
		return productImageUrl;
	}

	public void setProductImageUrl(String productImageUrl) {
		this.productImageUrl = productImageUrl;
	}

	public String getProductImageName() {
		return productImageName;
	}

	public void setProductImageName(String productImageName) {
		this.productImageName = productImageName;
	}

	public Date getManufacturingDate() {
		return manufacturingDate;
	}

	public void setManufacturingDate(Date manufacturingDate) {
		this.manufacturingDate = manufacturingDate;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Timestamp getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Timestamp modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getAuditLogonId() {
		return auditLogonId;
	}

	public void setAuditLogonId(String auditLogonId) {
		this.auditLogonId = auditLogonId;
	}

	public String getProductGroup() {
		return productGroup;
	}

	public void setProductGroup(String productGroup) {
		this.productGroup = productGroup;
	}
	
	public Double getTax() {
		return tax;
	}

	public void setTax(Double tax) {
		this.tax = tax;
	}

}
