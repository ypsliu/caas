/**
 * 
 */
package cn.rongcapital.caas.service.impl;

import cn.rongcapital.caas.po.User;

/**
 * @author wangshuguang
 *
 */
public interface NotificationService {
	public void notifyUser4Active(User user, String content);

	public void notifyUser4ResetPassword(User user, String content);

	public void notify4GeneralVCode(User user, String content);
}
