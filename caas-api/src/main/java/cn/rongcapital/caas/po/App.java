/**
 * 
 */
package cn.rongcapital.caas.po;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * 应用程序
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class App extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7434364929345860364L;

	/**
	 * 应用程序key
	 */
	@NotEmpty
	private String key;

	/**
	 * 应用程序私钥
	 */
	@NotEmpty
	private String secret;

	/**
	 * 是否校验sign
	 */
	@NotNull
	private Boolean checkSign = true;

	/**
	 * 授权超时秒数
	 */
	@NotNull
	private Long tokenTimeoutSec;

	/**
	 * 是否校验资源权限
	 */
	@NotNull
	private Boolean checkResource = true;

	/**
	 * 回调地址
	 */
	private String backUrl;
	/**
	 * 应用状态
	 */
	private String status;
	/**
	 * 应用类型
	 */
	private String appType;
	/**
	 * 校验验证码: 1 校验 0 不校验  -1 无设置
	 */
	private String checkVcode;
	
	/**
	 * app的用户是否需要邮件激活
	 */
	private Boolean emailNotify;

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the secret
	 */
	public String getSecret() {
		return secret;
	}

	/**
	 * @param secret
	 *            the secret to set
	 */
	public void setSecret(String secret) {
		this.secret = secret;
	}

	/**
	 * @return the checkSign
	 */
	public Boolean getCheckSign() {
		return checkSign;
	}

	/**
	 * @param checkSign
	 *            the checkSign to set
	 */
	public void setCheckSign(Boolean checkSign) {
		this.checkSign = checkSign;
	}

	/**
	 * @return the tokenTimeoutSec
	 */
	public Long getTokenTimeoutSec() {
		return tokenTimeoutSec;
	}

	/**
	 * @param tokenTimeoutSec
	 *            the tokenTimeoutSec to set
	 */
	public void setTokenTimeoutSec(Long tokenTimeoutSec) {
		this.tokenTimeoutSec = tokenTimeoutSec;
	}

	/**
	 * @return the checkResource
	 */
	public Boolean getCheckResource() {
		return checkResource;
	}

	/**
	 * @param checkResource
	 *            the checkResource to set
	 */
	public void setCheckResource(Boolean checkResource) {
		this.checkResource = checkResource;
	}

	/**
	 * @return the backUrl
	 */
	public String getBackUrl() {
		return backUrl;
	}

	/**
	 * @param backUrl
	 *            the backUrl to set
	 */
	public void setBackUrl(String backUrl) {
		this.backUrl = backUrl;
	}

	/***
	 * Get application type
	 * 
	 * @return appType
	 */
	public String getAppType() {
		return appType;
	}

	/***
	 * Set app type
	 * 
	 * @param Enum<AppType>
	 *            appType
	 */
	public void setAppType(String appType) {
		this.appType = appType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCheckVcode() {
		return checkVcode;
	}

	public void setCheckVcode(String checkVcode) {
		this.checkVcode = checkVcode;
	}
	
 

    /**
	 * @return the emailNotify
	 */
	public Boolean getEmailNotify() {
		return emailNotify;
	}

	/**
	 * @param emailNotify the emailNotify to set
	 */
	public void setEmailNotify(Boolean emailNotify) {
		this.emailNotify = emailNotify;
	}

	@Override
    public String toString() {
        return "App [key=" + key + ", secret=" + secret + ", checkSign=" + checkSign + ", tokenTimeoutSec="
                + tokenTimeoutSec + ", checkResource=" + checkResource + ", backUrl=" + backUrl + ", status=" + status
                + ", appType=" + appType + ", checkVcode=" + checkVcode + ", emailNotify=" + emailNotify + ", code="
                + code + ", name=" + name + ", comment=" + comment + ", creationUser=" + creationUser
                + ", creationTime=" + creationTime + ", updateUser=" + updateUser + ", updateTime=" + updateTime
                + ", version=" + version + ", removed=" + removed + "]";
    }
}
