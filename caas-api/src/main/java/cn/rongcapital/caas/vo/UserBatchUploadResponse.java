package cn.rongcapital.caas.vo;

import java.util.ArrayList;
import java.util.List;

import cn.rongcapital.caas.po.User;

/**
 * the response for user upload by csv
 * 
 * @author wangshuguang
 *
 */
public class UserBatchUploadResponse extends BaseResponse {
	private int noOfInserted;
	private int noOfUpdated;
	private int noOfFailed;
	private int noOfExisted;
	private int noOfInvalid;
	
	private String resultCode;

	private List<User> inserted;
	private List<User> updated;
	private List<User> failed;
	private List<User> existed;
	private List<User> invalid;

	public List<User> getInserted() {
		if (inserted == null) {
			inserted = new ArrayList<User>();
		}
		return inserted;

	}

	public void setInserted(List<User> inserted) {
		this.inserted = inserted;
	}

	public List<User> getUpdated() {
		if (updated == null) {
			updated = new ArrayList<User>();
		}
		return updated;
	}

	public void setUpdated(List<User> updated) {
		this.updated = updated;
	}

	public List<User> getFailed() {
		if (failed == null) {
			failed = new ArrayList<User>();
		}
		return failed;
	}

	public void setFailed(List<User> failed) {
		this.failed = failed;

	}

	public List<User> getExisted() {
		if (existed == null) {
			existed = new ArrayList<User>();
		}
		return existed;
	}

	public void setExisted(List<User> existed) {
		this.existed = existed;
	}

	public int getNoOfInserted() {
		return noOfInserted;
	}

	public void setNoOfInserted(int noOfInserted) {
		this.noOfInserted = noOfInserted;
	}

	public int getNoOfUpdated() {
		return noOfUpdated;
	}

	public void setNoOfUpdated(int noOfUpdated) {
		this.noOfUpdated = noOfUpdated;
	}

	public int getNoOfFailed() {
		return noOfFailed;
	}

	public void setNoOfFailed(int noOfFailed) {
		this.noOfFailed = noOfFailed;
	}

	public int getNoOfExisted() {
		return noOfExisted;
	}

	public void setNoOfExisted(int noOfExisted) {
		this.noOfExisted = noOfExisted;
	}

	public List<User> getInvalid() {
		if (invalid == null) {
			invalid = new ArrayList<User>();
		}
		return invalid;
	}

	public void setInvalid(List<User> invalid) {
		this.invalid = invalid;
	}

	public int getNoOfInvalid() {
		return noOfInvalid;
	}

	public void setNoOfInvalid(int noOfInvalid) {
		this.noOfInvalid = noOfInvalid;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}
	
	

}
