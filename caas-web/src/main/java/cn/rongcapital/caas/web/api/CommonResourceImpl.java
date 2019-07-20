/**
 * 
 */
package cn.rongcapital.caas.web.api;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import com.ruixue.serviceplatform.commons.utils.verificationcode.VerificationCode;
import com.ruixue.serviceplatform.commons.utils.verificationcode.VerificationCodeGenerator;

import cn.rongcapital.caas.api.CommonResource;
import cn.rongcapital.caas.api.SessionKeys;
import cn.rongcapital.caas.enums.RoleType;
import cn.rongcapital.caas.exception.UserNotFoundException;
import cn.rongcapital.caas.po.App;
import cn.rongcapital.caas.po.Role;
import cn.rongcapital.caas.service.AppService;
import cn.rongcapital.caas.service.RoleService;

/**
 * the implementation for CommonResource
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
@Controller
public final class CommonResourceImpl implements CommonResource {

	/**
	 * the current http request
	 */
	@Context
	private HttpServletRequest httpRequest;

	@Value("${verificationCode.length}")
	private int vCodeLength;

	@Value("${verificationImage.width}")
	private int vImageWidth;

	@Value("${verificationImage.height}")
	private int vImageHeight;

	@Autowired
	private AppService appService;
	@Autowired
	private RoleService roleService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.api.CommonResource#getVerificationImage()
	 */
	@Override
	public Response getVerificationImage() {
		return Response.status(Response.Status.OK).entity(this.writeVcode()).build();
	}

	@Override
	public Response getVerificationImageBase64() {
		return Response.status(Status.OK).entity(Base64.encodeBase64String(this.writeVcode()))
				.type(MediaType.TEXT_PLAIN_TYPE).build();
	}

	@Override
	public void getVerificationImageBase64Options() {
	}

	private byte[] writeVcode() {
		// make the verification code
		final VerificationCode vc = VerificationCodeGenerator.generate(
				VerificationCodeGenerator.generateRandomCode(this.vCodeLength), this.vImageWidth, this.vImageHeight);
		// save code to session
		this.httpRequest.getSession().setAttribute(SessionKeys.VERIFICATION_CODE, vc.getCode());
		// build the response
		ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);
		try {
			ImageIO.write(vc.getImage(), "png", baos);
			baos.flush();
			return baos.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException("get the verification image failed, error: " + e.getMessage(), e);
		} finally {
			try {
				baos.close();
			} catch (Exception e2) {
				//
			}
		}
	}

	/**
	 * @param httpRequest
	 *            the httpRequest to set
	 */
	public void setHttpRequest(final HttpServletRequest httpRequest) {
		this.httpRequest = httpRequest;
	}

	/**
	 * @param vCodeLength
	 *            the vCodeLength to set
	 */
	public void setvCodeLength(final int vCodeLength) {
		this.vCodeLength = vCodeLength;
	}

	/**
	 * @param vImageWidth
	 *            the vImageWidth to set
	 */
	public void setvImageWidth(final int vImageWidth) {
		this.vImageWidth = vImageWidth;
	}

	/**
	 * @param vImageHeight
	 *            the vImageHeight to set
	 */
	public void setvImageHeight(final int vImageHeight) {
		this.vImageHeight = vImageHeight;
	}

	@Override
	public App getAppInfo(String key) {

		// load
		final App app = this.appService.getAppByKey(key);
		if (app == null) {
			throw new UserNotFoundException("the app is NOT existed, key: " + key);
		}
		App newApp = new App();
		newApp.setCode(app.getCode());
		newApp.setKey(key);
		newApp.setBackUrl(app.getBackUrl());
		newApp.setName(app.getName());
		return newApp;

	}

	@Override
	public List<App> getPublicApps() {
		return appService.getPublicApps();

	}

	@Override
	public List<Role> getAppRoles(String appCode) {
		List<Role> roleList = roleService.getAppRoles(appCode);
		Iterator<Role> it = roleList.iterator();
		for (; it.hasNext();) {
			Role role = it.next();
			String roleType = role.getRoleType();
			if (RoleType.PRIVATE.toString().equals(roleType)) {
				//remove the private role
				it.remove();
			}
		}
		return roleList;
	}

	

}
