package cn.rongcapital.caas.vo.ipa;

import org.hibernate.validator.constraints.NotEmpty;

public class IPAForm {
	/**
	 * 用户名
	 */
	@NotEmpty
	private String username;
	/**
	 * 密码
	 */
	@NotEmpty
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
