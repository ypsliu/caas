/**
 * 
 */
package cn.rongcapital.caas.web.api;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import cn.rongcapital.caas.api.SessionKeys;
import cn.rongcapital.caas.api.ValidationResource;
import cn.rongcapital.caas.service.AdminUserService;
import cn.rongcapital.caas.service.AppService;
import cn.rongcapital.caas.service.CommonVCodeService;
import cn.rongcapital.caas.service.OperationService;
import cn.rongcapital.caas.service.ResourceService;
import cn.rongcapital.caas.service.RoleService;
import cn.rongcapital.caas.service.SubjectService;
import cn.rongcapital.caas.service.UserService;
import cn.rongcapital.caas.vo.ValidationResult;

/**
 * the implementation for ValidationResource
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
@Controller
public final class ValidationResourceImpl implements ValidationResource {

	/**
	 * logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ValidationResourceImpl.class);

	@Autowired(required = false)
	private AdminUserService adminUserService;

	@Autowired(required = false)
	private AppService appService;

	@Autowired(required = false)
	private ResourceService resourceService;

	@Autowired(required = false)
	private RoleService roleService;

	@Autowired(required = false)
	private UserService userService;

	@Autowired(required = false)
	private SubjectService subjectService;

	@Autowired(required = false)
	private OperationService operationService;

	@Value("${validation.errorCode.duplicate}")
	private String duplicateErrorCode;

	@Value("${validation.errorCode.invalid}")
	private String invalidErrorCode;

	@Value("${validation.errorCode.unknown}")
	private String unknownErrorCode;

	@Value("${ipa.check.enabled}")
	private boolean ipaEnabled;

	@Context
	private HttpServletRequest httpRequest;

	@Autowired(required = false)
	private CommonVCodeService commonVcodeService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.rongcapital.caas.api.ValidationResource#validateAppName(java.lang.
	 * String)
	 */
	@Override
	public ValidationResult validateAppName(final String name) {
		final ValidationResult r = new ValidationResult();
		r.setSuccess(true);
		try {
			if (this.appService.existsByName(name)) {
				r.setSuccess(false);
				r.setErrorCode(this.duplicateErrorCode);
				r.setErrorMessage("the application already existed");
			}
		} catch (Exception e) {
			LOGGER.error("validate the app name failed, name: " + name + ", error: " + e.getMessage(), e);
			r.setSuccess(false);
			r.setErrorCode(this.unknownErrorCode);
			r.setErrorMessage(e.getMessage());
		}
		return r;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.rongcapital.caas.api.ValidationResource#validateRoleName(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public ValidationResult validateRoleName(final String appCode, final String name) {
		final ValidationResult r = new ValidationResult();
		r.setSuccess(true);
		try {
			if (this.roleService.existsByName(appCode, name)) {
				r.setSuccess(false);
				r.setErrorCode(this.duplicateErrorCode);
				r.setErrorMessage("the role of this application already existed");
			}
		} catch (Exception e) {
			LOGGER.error("validate the role name failed, appCode: " + appCode + ", name: " + name + ", error: "
					+ e.getMessage(), e);
			r.setSuccess(false);
			r.setErrorCode(this.unknownErrorCode);
			r.setErrorMessage(e.getMessage());
		}
		return r;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.rongcapital.caas.api.ValidationResource#validateUserName(java.lang.
	 * String)
	 */
	@Override
	public ValidationResult validateUserName(final String name) {
		final ValidationResult r = new ValidationResult();
		r.setSuccess(true);
		try {
			if (this.userService.getUserByName(name) != null || (ipaEnabled && this.userService
					.getIpaUserByName(this.userService.getIpaUsers(null).getResult(), name) != null)) {
				r.setSuccess(false);
				r.setErrorCode(this.duplicateErrorCode);
				r.setErrorMessage("the user already existed");
			}
		} catch (Exception e) {
			LOGGER.error("validate the user name failed, name: " + name + ", error: " + e.getMessage(), e);
			r.setSuccess(false);
			r.setErrorCode(this.unknownErrorCode);
			r.setErrorMessage(e.getMessage());
		}
		return r;
	}

	@Override
	public void validateUserName() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.rongcapital.caas.api.ValidationResource#validateAdminUserName(java.
	 * lang.String)
	 */
	@Override
	public ValidationResult validateAdminUserName(final String name) {
		final ValidationResult r = new ValidationResult();
		r.setSuccess(true);
		try {
			if (this.adminUserService.existsByName(name) || (ipaEnabled && this.userService
					.getIpaUserByName(this.userService.getIpaUsers(null).getResult(), name) != null)) {
				r.setSuccess(false);
				r.setErrorCode(this.duplicateErrorCode);
				r.setErrorMessage("the adminUser already existed");
			}
		} catch (Exception e) {
			LOGGER.error("validate the adminUser name failed, name: " + name + ", error: " + e.getMessage(), e);
			r.setSuccess(false);
			r.setErrorCode(this.unknownErrorCode);
			r.setErrorMessage(e.getMessage());
		}
		return r;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.rongcapital.caas.api.ValidationResource#validateUserEmail(java.lang.
	 * String)
	 */
	@Override
	public ValidationResult validateUserEmail(final String email) {
		final ValidationResult r = new ValidationResult();
		r.setSuccess(true);
		try {
			if (this.userService.getUserByEmail(email) != null || (ipaEnabled && this.userService
					.getIpaUserByEmail(this.userService.getIpaUsers(null).getResult(), email) != null)) {
				r.setSuccess(false);
				r.setErrorCode(this.duplicateErrorCode);
				r.setErrorMessage("the email already existed");
			}
		} catch (Exception e) {
			LOGGER.error("validate the user email failed, email: " + email + ", error: " + e.getMessage(), e);
			r.setSuccess(false);
			r.setErrorCode(this.unknownErrorCode);
			r.setErrorMessage(e.getMessage());
		}
		return r;
	}

	@Override
	public void validateUserEmail() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.rongcapital.caas.api.ValidationResource#validateAdminUserEmail(java.
	 * lang.String)
	 */
	@Override
	public ValidationResult validateAdminUserEmail(final String email) {
		final ValidationResult r = new ValidationResult();
		r.setSuccess(true);
		try {
			if (this.adminUserService.getAdminUserByEmail(email) != null || (ipaEnabled && this.userService
					.getIpaUserByEmail(this.userService.getIpaUsers(null).getResult(), email) != null)) {
				r.setSuccess(false);
				r.setErrorCode(this.duplicateErrorCode);
				r.setErrorMessage("the email already existed");
			}
		} catch (Exception e) {
			LOGGER.error("validate the adminUser email failed, email: " + email + ", error: " + e.getMessage(), e);
			r.setSuccess(false);
			r.setErrorCode(this.unknownErrorCode);
			r.setErrorMessage(e.getMessage());
		}
		return r;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.rongcapital.caas.api.ValidationResource#validateUserMobile(java.lang.
	 * String)
	 */
	@Override
	public ValidationResult validateUserMobile(final String mobile) {
		final ValidationResult r = new ValidationResult();
		r.setSuccess(true);
		try {
			if (this.userService.getUserByMobile(mobile) != null) {
				r.setSuccess(false);
				r.setErrorCode(this.duplicateErrorCode);
				r.setErrorMessage("the mobile already existed");
			}
		} catch (Exception e) {
			LOGGER.error("validate the user mobile failed, mobile: " + mobile + ", error: " + e.getMessage(), e);
			r.setSuccess(false);
			r.setErrorCode(this.unknownErrorCode);
			r.setErrorMessage(e.getMessage());
		}
		return r;
	}

	@Override
	public void validateUserMobile() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.rongcapital.caas.api.ValidationResource#validateVerificationCode(java.
	 * lang.String)
	 */
	@Override
	public ValidationResult validateVerificationCode(final String vcode) {
		final ValidationResult r = new ValidationResult();
		r.setSuccess(true);
		try {
			// check common vcode
			String commonVcode = commonVcodeService.getCode();
			if (!StringUtils.isEmpty(commonVcode) && !StringUtils.isEmpty(vcode)
					&& commonVcode.equalsIgnoreCase(vcode)) {
				return r;
			}
			final String vcodeInSession = (String) this.httpRequest.getSession()
					.getAttribute(SessionKeys.VERIFICATION_CODE);
			if (vcode == null || vcodeInSession == null || !vcode.equalsIgnoreCase(vcodeInSession)) {
				r.setSuccess(false);
				r.setErrorCode(this.invalidErrorCode);
				r.setErrorMessage("invalid verification code");
			}
		} catch (Exception e) {
			LOGGER.error("validate the verification code failed, vcode: " + vcode + ", error: " + e.getMessage(), e);
			r.setSuccess(false);
			r.setErrorCode(this.unknownErrorCode);
			r.setErrorMessage(e.getMessage());
		}
		return r;
	}

	@Override
	public void validateVerificationCode() {
	}

	/**
	 * @param adminUserService
	 *            the adminUserService to set
	 */
	public void setAdminUserService(final AdminUserService adminUserService) {
		this.adminUserService = adminUserService;
	}

	/**
	 * @param appService
	 *            the appService to set
	 */
	public void setAppService(final AppService appService) {
		this.appService = appService;
	}

	/**
	 * @param resourceService
	 *            the resourceService to set
	 */
	public void setResourceService(final ResourceService resourceService) {
		this.resourceService = resourceService;
	}

	/**
	 * @param roleService
	 *            the roleService to set
	 */
	public void setRoleService(final RoleService roleService) {
		this.roleService = roleService;
	}

	/**
	 * @param userService
	 *            the userService to set
	 */
	public void setUserService(final UserService userService) {
		this.userService = userService;
	}

	/**
	 * @param duplicateErrorCode
	 *            the duplicateErrorCode to set
	 */
	public void setDuplicateErrorCode(final String duplicateErrorCode) {
		this.duplicateErrorCode = duplicateErrorCode;
	}

	/**
	 * @param invalidErrorCode
	 *            the invalidErrorCode to set
	 */
	public void setInvalidErrorCode(final String invalidErrorCode) {
		this.invalidErrorCode = invalidErrorCode;
	}

	/**
	 * @param unknownErrorCode
	 *            the unknownErrorCode to set
	 */
	public void setUnknownErrorCode(final String unknownErrorCode) {
		this.unknownErrorCode = unknownErrorCode;
	}

	/**
	 * @param httpRequest
	 *            the httpRequest to set
	 */
	public void setHttpRequest(final HttpServletRequest httpRequest) {
		this.httpRequest = httpRequest;
	}

	@Override
	public ValidationResult validateSubjectName(String appCode, String name) {
		final ValidationResult r = new ValidationResult();
		r.setSuccess(true);
		try {
			if (this.subjectService.existsByName(appCode, name)) {
				r.setSuccess(false);
				r.setErrorCode(this.duplicateErrorCode);
				r.setErrorMessage("the role of this application already existed");
			}
		} catch (Exception e) {
			LOGGER.error("validate the role name failed, appCode: " + appCode + ", name: " + name + ", error: "
					+ e.getMessage(), e);
			r.setSuccess(false);
			r.setErrorCode(this.unknownErrorCode);
			r.setErrorMessage(e.getMessage());
		}
		return r;
	}

	@Override
	public ValidationResult validateOperationName(String appCode, String name) {
		final ValidationResult r = new ValidationResult();
		r.setSuccess(true);
		try {
			if (this.operationService.existsByName(appCode, name)) {
				r.setSuccess(false);
				r.setErrorCode(this.duplicateErrorCode);
				r.setErrorMessage("the role of this application already existed");
			}
		} catch (Exception e) {
			LOGGER.error("validate the role name failed, appCode: " + appCode + ", name: " + name + ", error: "
					+ e.getMessage(), e);
			r.setSuccess(false);
			r.setErrorCode(this.unknownErrorCode);
			r.setErrorMessage(e.getMessage());
		}
		return r;
	}
}
