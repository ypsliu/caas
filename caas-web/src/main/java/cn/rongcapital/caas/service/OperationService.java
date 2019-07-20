package cn.rongcapital.caas.service;

import java.util.List;

import cn.rongcapital.caas.po.AdminUser;
import cn.rongcapital.caas.po.Operation;

/**
 * the Operation service
 * 
 * @author wang shu guang
 *
 */
public interface OperationService {
	/**
	 * to create the Operation
	 * 
	 * @param Operation
	 *            the creating Operation
	 * @param creatingBy
	 *            the creating by
	 * @return the created Operation
	 */
	Operation createOperation(Operation Operation, AdminUser creatingBy);

	/**
	 * to get the Operation by code
	 * 
	 * @param code
	 *            the Operation code
	 * @return the Operation
	 */
	Operation getOperation(String code);

	/**
	 * to get all Operations for the application
	 * 
	 * @param appCode
	 * 
	 * @return the Operations
	 */
	List<Operation> getAppOperations(String appCode);

	/**
	 * to update the Operation
	 * 
	 * @param Operation
	 *            the updating Operation
	 * @param updatingBy
	 *            the updating by
	 */
	void updateOperation(Operation Operation, AdminUser updatingBy);

	/**
	 * to remove the Operation
	 * 
	 * @param code
	 *            the Operation code
	 * @param removingBy
	 *            the removing by
	 */
	void removeOperation(String code, AdminUser removingBy);

	Operation getOperationByName(Operation Operation);

	boolean existsByName(String appCode, String name);
}
