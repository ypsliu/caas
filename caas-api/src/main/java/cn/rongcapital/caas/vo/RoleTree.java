package cn.rongcapital.caas.vo;

import java.util.List;

import cn.rongcapital.caas.po.Role;

/**
 * RoleTree This VO is used for creating the role tree module for a Application.
 * 
 * @author wangshuguang
 *
 */
public class RoleTree {
	private String code;
	private String name;
	private List<RoleTree> children;

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the children
	 */
	public List<RoleTree> getChildren() {
		return children;
	}

	/**
	 * @param children
	 *            the children to set
	 */
	public void setChildren(List<RoleTree> children) {
		this.children = children;
	}

}
