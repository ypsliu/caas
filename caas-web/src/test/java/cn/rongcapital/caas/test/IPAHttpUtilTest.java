package cn.rongcapital.caas.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.rongcapital.caas.util.IPAHttpUtil;
import cn.rongcapital.caas.vo.ipa.IPAHttpResponse;

public class IPAHttpUtilTest {
	private String username = "sgwang";
	private String password = "11111111";

	private static final String user_find = "user_find";
	private static final String user_show = "user_show";

	@Test
	public void login() {
		try {
			IPAHttpUtil util = initUtil();
			String cookie = util.login(username, password);
			System.out.println("cookie:" + cookie);
			Assert.assertNotNull(cookie);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void userFind() {
		String[] un = new String[1];
		un[0] = username;
		try {
			IPAHttpUtil util = initUtil();
			String cookie = util.login(username, password);
			IPAHttpResponse result = util.execute(IPAHttpUtilTest.user_find, un, null, "0", cookie);
			int statusCode = result.getStatusCode();
			Assert.assertEquals(statusCode, 200);
			System.out.println("=====================測試結果========================");
			System.out.println(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void userShow() {
		String[] un = new String[1];
		Map<String, Object> options = new HashMap<String, Object>();
		options.put("all", true);
		un[0] = username;
		try {
			IPAHttpUtil util = initUtil();
			String cookie = util.login(username, password);
			IPAHttpResponse result = util.execute(IPAHttpUtilTest.user_show, un, options, "0", cookie);
			System.out.println("=====================測試結果========================");
			System.out.println("cookie:"+cookie);
			System.out.println(result.getResponseBody());
			JSONObject resultobj = JSONObject.parseObject(result.getResponseBody());
			JSONObject resultresult = resultobj.getJSONObject("result");
			resultresult = resultresult.getJSONObject("result");
			int statusCode = result.getStatusCode();
			
			JSONArray uid = resultresult.getJSONArray("uid");
			JSONArray mail = resultresult.getJSONArray("mail");
			JSONArray memberof_group = resultresult.getJSONArray("memberof_group");
			
			Assert.assertEquals(statusCode, 200);
			System.out.println(uid.toString() + "==" + uid.size());
			System.out.println(mail + "==" + mail.size());
			System.out.println(memberof_group + "==" + memberof_group.size());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private IPAHttpUtil initUtil() {
		IPAHttpUtil util = new IPAHttpUtil();
		String url = "https://ipaserver.sgwang.com/ipa";
		util.setServerUrl(url);
		util.setAPIVersion("2.117");
		return util;
	}
}
