/**
 * 
 */
package cn.rongcapital.caas.loadtest;

/**
 * the test step
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public enum TestStep {

	LOGIN("login"),

	AUTH("auth"),

	GET_USERINFO("getUserInfo"),

	CHECK_AUTH("checkAuth"),

	REFRESH_TOKEN("refreshToken"),

	LOGOUT("logout"),

	RESET_PASSWORD("resetPassword"),

	REGISTER("register"),

	UPDATE_USER("updateUser"),

	GET_AUTH_CODE("getAuthCode");

	private final String name;

	private TestStep(final String name) {
		this.name = name;
	}

	/**
	 * to get the step name
	 * 
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

}
