package cn.rongcapital.caas.vo;

import java.util.Date;

public class UserImportResult {
	private String code;
	private String appCode;
	private byte[] inserted;
	private byte[] updated;
	private byte[] invalid;
	private byte[] existed;
	private byte[] failed;

	private String status;
	private String comment;
	private String creationUser;
	private Date creationTime;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public byte[] getInserted() {
		return inserted;
	}

	public void setInserted(byte[] inserted) {
		this.inserted = inserted;
	}

	public byte[] getUpdated() {
		return updated;
	}

	public void setUpdated(byte[] updated) {
		this.updated = updated;
	}

	public byte[] getInvalid() {
		return invalid;
	}

	public void setInvalided(byte[] invalid) {
		this.invalid = invalid;
	}

	public byte[] getExisted() {
		return existed;
	}

	public void setExisted(byte[] existed) {
		this.existed = existed;
	}

	public byte[] getFailed() {
		return failed;
	}

	public void setFailed(byte[] failed) {
		this.failed = failed;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getCreationUser() {
		return creationUser;
	}

	public void setCreationUser(String creationUser) {
		this.creationUser = creationUser;
	}

	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

}
