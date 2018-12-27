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
@Table(name = "admin_account")
@NamedQuery(name = "AdminAccount.findAll", query = "SELECT aa FROM AdminAccount aa")
public class AdminAccount implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "admin_id", unique = true, nullable = false)
	private Integer adminId;
	
	@Column(name = "admin_name", nullable = false)
	private String adminName;
	
	@Column(name = "password", nullable = false)
	private String password;
	
	@Column(name = "email_address", nullable = false)
	private String emailAddress;
	
	@Column(name = "reset_token", nullable = false)
	private String resetToken;
	
	@Column(name = "is_2fa_enable", nullable = false)
	private boolean is2faEnable;

	
	public Integer getAdminId() {
		return adminId;
	}

	public void setAdminId(Integer adminId) {
		this.adminId = adminId;
	}

	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getResetToken() {
		return resetToken;
	}

	public void setResetToken(String resetToken) {
		this.resetToken = resetToken;
	}

	public boolean getIsIs2faEnable() {
		return is2faEnable;
	}

	public void setIs2faEnable(boolean is2faEnable) {
		this.is2faEnable = is2faEnable;
	}
	
	
}
