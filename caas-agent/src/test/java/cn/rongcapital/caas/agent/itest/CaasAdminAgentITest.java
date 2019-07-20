/**
 * 
 */
package cn.rongcapital.caas.agent.itest;

import java.io.File;

import org.junit.Test;

import cn.rongcapital.caas.agent.admin.CaasAdminAgent;
import cn.rongcapital.caas.po.AdminUser;

/**
 * @author shangchunming@rongcapital.cn
 *
 */
public class CaasAdminAgentITest {

	@Test
	public void test() {
		// create the agent
		final CaasAdminAgent agent = new CaasAdminAgent();
		agent.setSettingsYamlFile(System.getProperty("APP_HOME") + File.separator + "conf" + File.separator
				+ "test-caas-admin-agent-settings.yaml");
		// start the agent
		agent.start();


		// current admin user
		AdminUser adminUser = agent.currentAdminUser();
		System.out.println("adminUser: " + adminUser);

		// logout
		agent.stop();
	}

}
