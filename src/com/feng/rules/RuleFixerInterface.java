/* 
 * (C) Copyright TCS 2017 to present. All rights reserved.
 */
package com.feng.rules;

/**
 * Class Description - This interface is used to standardized the rule
 * implementation classes.
 *
 *
 * @author User
 * @version 1.0
 * Sep 7, 2017
 */
public interface RuleFixerInterface {

	/**
	 * 
	 * Description - This method must be implemented by the implementing rule
	 * classes. This method in the implemented classes will be executed by
	 * the rule fix framework using reflections.
	 *
	 *
	 *
	 * @author User
	 * @param targetPath The path to the target project in the workspace.
	 */
	void fixRule(String targetPath);
	
}
