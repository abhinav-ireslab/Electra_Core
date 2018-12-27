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
@Table(name = "gst_hsn_code")
@NamedQuery(name = "GstHsnCode.findAll", query = "SELECT g FROM GstHsnCode g")
public class GstHsnCode implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "gst_hsn_id", unique = true, nullable = false)
	private Integer gstHsnId;
	
	@Column(name = "hsn_code", nullable = false)
	private String hsnCode;
	
	@Column(name = "description", nullable = false)
	private String description;
	
	@Column(name = "cgst", nullable = false)
	private Double cgst;
	
	@Column(name = "sgst_utgst", nullable = false)
	private Double sgstUtgst;
	
	@Column(name = "igst", nullable = false)
	private Double igst;
	
	@Column(name = "related_export_import_hsn_code", nullable = false)
	private String relatedExportImportHsnCode;
	
	@Column(name = "chapter_no", nullable = false)
	private String chapterNo;

	public Integer getGstHsnId() {
		return gstHsnId;
	}

	public void setGstHsnId(Integer gstHsnId) {
		this.gstHsnId = gstHsnId;
	}

	public String getHsnCode() {
		return hsnCode;
	}

	public void setHsnCode(String hsnCode) {
		this.hsnCode = hsnCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getCgst() {
		return cgst;
	}

	public void setCgst(Double cgst) {
		this.cgst = cgst;
	}

	

	public Double getSgstUtgst() {
		return sgstUtgst;
	}

	public void setSgstUtgst(Double sgstUtgst) {
		this.sgstUtgst = sgstUtgst;
	}

	public Double getIgst() {
		return igst;
	}

	public void setIgst(Double igst) {
		this.igst = igst;
	}

	public String getRelatedExportImportHsnCode() {
		return relatedExportImportHsnCode;
	}

	public void setRelatedExportImportHsnCode(String relatedExportImportHsnCode) {
		this.relatedExportImportHsnCode = relatedExportImportHsnCode;
	}

	public String getChapterNo() {
		return chapterNo;
	}

	public void setChapterNo(String chapterNo) {
		this.chapterNo = chapterNo;
	}

	
	
}
