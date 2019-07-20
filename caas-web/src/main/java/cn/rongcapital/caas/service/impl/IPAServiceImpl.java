package cn.rongcapital.caas.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.rongcapital.caas.exception.NotAuthorizedException;
import cn.rongcapital.caas.service.RemoteAccountService;
import cn.rongcapital.caas.util.IPAHttpUtil;
import cn.rongcapital.caas.vo.Account;
import cn.rongcapital.caas.vo.ipa.IPAHttpResponse;
import cn.rongcapital.caas.vo.ipa.IPARequest;

/**
 * @author wangshuguang
 */
@Service(value = "ipaService")
public class IPAServiceImpl implements RemoteAccountService<IPARequest> {

	@Autowired
	private IPAHttpUtil iPAHttpUtils;

	@Override
	public String login(String name, String pwd) {
		String content = null;
		try {
			content = iPAHttpUtils.login(name, pwd);
		} catch (Exception e) {
			throw new NotAuthorizedException(e);
		}
		return content;
	}

	private IPAHttpResponse execute(IPARequest request) {
		String method = request.getMethod();
		String[] params = request.getParams();
		Map<String, Object> options = request.getOptions();
		String id = request.getId();
		String cookie = request.getCookie();
		IPAHttpResponse response = null;
		try {
			response = iPAHttpUtils.execute(method, params, options, id, cookie);
		} catch (Exception e) {
			throw new RuntimeException("IPA access error with request" + request.toString(), e);
		}
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.rongcapital.caas.service.RemoteAccountService#getAccountDetails(java.
	 * lang.Object)
	 */
	@Override
	public Account getAccountDetails(IPARequest request) {

		IPAHttpResponse response = execute(request);
		Account account = null;
		if (response.isSuccess()) {
			account = new Account();
			String ipaRespBody = response.getResponseBody();

			JSONObject resultobj = JSONObject.parseObject(ipaRespBody);
			JSONObject resultL1 = resultobj.getJSONObject("result");
			JSONObject resultL2 = resultL1.getJSONObject("result");
			// check group
			JSONArray jsongroups = resultL2.getJSONArray("memberof_group");
			int groupNo = jsongroups.size();
			if (groupNo >= 0) {
				String[] groups = new String[groupNo];
				for (int i = 0; i < groupNo; i++) {
					groups[i] = jsongroups.getString(i);
				}
				account.setGroups(groups);

			}

		}
		return account;
	}

	//
	// public static void main(String[] args) {
	// try {
	// IPAServiceImpl s = new IPAServiceImpl();
	//
	// String result = s.login("wangshuguang", "11111111");
	// System.out.println(result);
	// System.out.println("done");
	// } catch (Exception e) {
	// // TODO: handle exception
	// e.printStackTrace();
	// }
	//
	// }

}
