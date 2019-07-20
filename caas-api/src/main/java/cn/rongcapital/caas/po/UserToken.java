/**
 * 
 */
package cn.rongcapital.caas.po;

/**
 * @author wangshuguang
 *
 */
public class UserToken extends BaseEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8541966369961321064L;
	private String userCode;
	private String token;

	/**
	 * @return the userCode
	 */
	public String getUserCode() {
		return userCode;
	}

	/**
	 * @param userCode
	 *            the userCode to set
	 */
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token
	 *            the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

}
