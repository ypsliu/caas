/**
 * 
 */
package cn.rongcapital.caas.agent.itest;

import java.util.ArrayList;
import java.util.List;

import cn.rongcapital.caas.agent.AccessTokenInfo;
import cn.rongcapital.caas.agent.CaasAgent;
import cn.rongcapital.caas.agent.UserAuthStatus;
import cn.rongcapital.caas.agent.UserBatchAuthStatus;
import cn.rongcapital.caas.agent.UserInfo;

/**
 * the sample for CaasAgent
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public class CaasAgentSample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// 认证code，应该从用户登录后的结果中获取，通常来自于登录完成后的重定向
		String authCode = "the authCode from user login result";

		// 创建agent实例
		CaasAgent agent = new CaasAgent();

		// 指定配置文件
		agent.setSettingsYamlFile("your settings file, full path with file name");

		// 启动agent
		agent.start();

		// 用户授权，所得的token信息应该保存在用户session里
		AccessTokenInfo token = agent.userAuth(authCode);
		System.out.println("the token: " + token);

		// 获取用户信息，使用该返回值中的信息与自身用户系统的用户进行对应
		UserInfo userInfo = agent.getUserInfo(token.getAccessToken());
		System.out.println("userInfo: " + userInfo);

		// 校验授权
		String operation="1";
		UserAuthStatus status = agent.checkAuth(token.getAccessToken(), "your protected resource code",operation);
		boolean tokenRefreshFlag = status.isTokenRefreshFlag();
		if (!status.isSuccess()) {
			// 授权校验失败，此时应该通知用户重新登录
		}

		// 更新token，需要用新的token替换原来保存在用户session中的token信息
		if (tokenRefreshFlag) {
			token = agent.refreshToken(token.getRefreshToken());
			System.out.println("the new token: " + token);
		}

		// 批量校验授权
		List<String> resourceCodes = new ArrayList<String>();
		resourceCodes.add("your protected resource code 1");
		resourceCodes.add("your protected resource code 2");
		UserBatchAuthStatus batchStatus = agent.batchCheckAuth(token.getAccessToken(), resourceCodes, "查询");
		tokenRefreshFlag = batchStatus.isTokenRefreshFlag();
		if (!batchStatus.isSuccess()) {
			// 授权校验失败，此时应该通知用户重新登录
		}
		// 已授权的资源code列表
		batchStatus.getResourceCodes();

		// 更新token，需要用新的token替换原来保存在用户session中的token信息
		if (tokenRefreshFlag) {
			token = agent.refreshToken(token.getRefreshToken());
			System.out.println("the new token: " + token);
		}

		// 注销
		if (agent.userLogout(token.getAccessToken())) {
			// 用户注销成功，此时应该引导用户重定向
		}

		// 重置密码
		if (agent.userResetPassword("user login name from the user request", "user new password")) {
			// 用户重置密码成功
		}

		// 重新获取授权code
		authCode = agent.getAuthCode(token.getAccessToken());
		System.out.println("the new authCode: " + authCode);

		// 停止agent
		agent.stop();
	}

}
