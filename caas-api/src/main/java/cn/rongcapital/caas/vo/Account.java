/**
 * 
 */
package cn.rongcapital.caas.vo;

/**
 * @author wangshuguang
 *
 */
public class Account {
	private String accountName;
	private String[] groups;

	/**
	 * @return the accountName
	 */
	public String getAccountName() {
		return accountName;
	}

	/**
	 * @param accountName
	 *            the accountName to set
	 */
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	/**
	 * @return the groups
	 */
	public String[] getGroups() {
		return groups;
	}

	/**
	 * @param groups
	 *            the groups to set
	 */
	public void setGroups(String[] groups) {
		this.groups = groups;
	}

}
