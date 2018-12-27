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
@Table(name = "product_group_relation")
@NamedQuery(name = "ProductGroupRelation.findAll", query = "SELECT pgr FROM ProductGroupRelation pgr")
public class ProductGroupRelation implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "relation_id", unique = true, nullable = false)
	private Integer relationId;
	
	@Column(name = "product_id", nullable = false)
	private Integer productId;
	
	@Column(name = "group_id", nullable = false)
	private Integer groupId;
	
	@Column(name = "created_date", nullable = false)
	private Date createdDate;
	
	@Column(name = "modified_date", nullable = false)
	private Timestamp modifiedDate;
	
	@Column(name = "audit_logon_id", nullable = true)
	private String auditLogonId;

	public ProductGroupRelation() {
		
	}

	public Integer getRelationId() {
		return relationId;
	}

	public void setRelationId(Integer relationId) {
		this.relationId = relationId;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
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
