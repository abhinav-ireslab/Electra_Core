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
@Table(name = "gst_sac_code")
@NamedQuery(name = "GstSacCode.findAll", query = "SELECT g FROM GstSacCode g")
public class GstSacCode implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "gst_sac_id", unique = true, nullable = false)
	private Integer gstSacId;
	
	@Column(name = "sac_code", nullable = false)
	private String sacCode;
	
	@Column(name = "description", nullable = false)
	private String description;
	
	@Column(name = "cgst", nullable = false)
	private Double cgst;
	
	@Column(name = "sgst_utgst", nullable = false)
	private Double sgstUtgst;
	
	@Column(name = "igst", nullable = false)
	private Double igst;
	
	@Column(name = "also_check", nullable = false)
	private String alsoCheck;
	
	@Column(name = "chapter_no", nullable = false)
	private String chapterNo;

	public Integer getGstSacId() {
		return gstSacId;
	}

	public void setGstSacId(Integer gstSacId) {
		this.gstSacId = gstSacId;
	}

	public String getSacCode() {
		return sacCode;
	}

	public void setSacCode(String sacCode) {
		this.sacCode = sacCode;
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

	public String getAlsoCheck() {
		return alsoCheck;
	}

	public void setAlsoCheck(String alsoCheck) {
		this.alsoCheck = alsoCheck;
	}

	public String getChapterNo() {
		return chapterNo;
	}

	public void setChapterNo(String chapterNo) {
		this.chapterNo = chapterNo;
	}
	
	
	
}
