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
@Table(name = "gst_chapter")
@NamedQuery(name = "GSTChapter.findAll", query = "SELECT g FROM GSTChapter g")
public class GSTChapter implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "gst_chapter_id", unique = true, nullable = false)
	private Integer gstChapterId;
	
	@Column(name = "chapter_description", nullable = false)
	private String chapterDescription;
	
	@Column(name = "chapter_no", nullable = false)
	private String chapterNumber;

	public Integer getGstChapterId() {
		return gstChapterId;
	}

	public void setGstChapterId(Integer gstChapterId) {
		this.gstChapterId = gstChapterId;
	}

	public String getChapterDescription() {
		return chapterDescription;
	}

	public void setChapterDescription(String chapterDescription) {
		this.chapterDescription = chapterDescription;
	}

	public String getChapterNumber() {
		return chapterNumber;
	}

	public void setChapterNumber(String chapterNumber) {
		this.chapterNumber = chapterNumber;
	}

	
	
}
