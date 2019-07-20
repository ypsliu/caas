/**
 * 
 */
package cn.rongcapital.caas.po;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * the base entity
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public abstract class BaseEntity implements Serializable {

	private static final long serialVersionUID = 5064109552686850103L;

	/**
	 * 编码
	 */
	protected String code;

	/**
	 * 名称
	 */
	@NotEmpty
	protected String name;

	/**
	 * 备注
	 */
	protected String comment;

	/**
	 * 创建用户
	 */
	protected String creationUser;

	/**
	 * 创建时间
	 */
	protected Date creationTime;

	/**
	 * 更新用户
	 */
	protected String updateUser;

	/**
	 * 更新时间
	 */
	protected Date updateTime;

	/**
	 * 当前版本
	 */
	protected Long version;
	/**
	 * 是否删除
	 */
	protected boolean removed;

	/**
	 * @return the code
	 */
	public final String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public final void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public final void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the comment
	 */
	public final String getComment() {
		return comment;
	}

	/**
	 * @param comment
	 *            the comment to set
	 */
	public final void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return the creationUser
	 */
	public final String getCreationUser() {
		return creationUser;
	}

	/**
	 * @param creationUser
	 *            the creationUser to set
	 */
	public final void setCreationUser(String creationUser) {
		this.creationUser = creationUser;
	}

	/**
	 * @return the creationTime
	 */
	public final Date getCreationTime() {
		return creationTime;
	}

	/**
	 * @param creationTime
	 *            the creationTime to set
	 */
	public final void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	/**
	 * @return the updateUser
	 */
	public final String getUpdateUser() {
		return updateUser;
	}

	/**
	 * @param updateUser
	 *            the updateUser to set
	 */
	public final void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	/**
	 * @return the updateTime
	 */
	public final Date getUpdateTime() {
		return updateTime;
	}

	/**
	 * @param updateTime
	 *            the updateTime to set
	 */
	public final void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	/**
	 * @return the version
	 */
	public final Long getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public final void setVersion(Long version) {
		this.version = version;
	}

	public boolean isRemoved() {
		return removed;
	}

	public void setRemoved(boolean removed) {
		this.removed = removed;
	}

}
