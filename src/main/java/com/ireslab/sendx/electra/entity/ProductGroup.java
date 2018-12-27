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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author ireslab
 *
 */
/**
 * The persistent class for the product_purchase_history database table.
 * 
 */
@Entity
@Table(name = "product_group")
@NamedQuery(name = "ProductGroup.findAll", query = "SELECT pg FROM ProductGroup pg")
public class ProductGroup implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "group_id", unique = true, nullable = false)
	private Integer groupId;
	
	@Column(name = "product_group_name", nullable = false)
	private String productGroupName;
	
	@Column(name = "product_group_description", nullable = false)
	private String productGroupDescription;
	
	@Column(name = "created_date", nullable = false)
	private Date createdDate;
	
	@Column(name = "modified_date", nullable = false)
	private Timestamp modifiedDate;
	
	@Column(name = "audit_logon_id", nullable = true)
	private String auditLogonId;
	
	public ProductGroup() {
		
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public String getProductGroupName() {
		return productGroupName;
	}

	public void setProductGroupName(String productGroupName) {
		this.productGroupName = productGroupName;
	}

	public String getProductGroupDescription() {
		return productGroupDescription;
	}

	public void setProductGroupDescription(String productGroupDescription) {
		this.productGroupDescription = productGroupDescription;
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
	
	

}
