/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
package org.ebayopensource.turmeric.policyservice.provider;

import org.ebayopensource.turmeric.utils.config.PolicyProvider;

/**
 * The Interface AuthenticationPolicyProvider.
 */
interface AuthenticationPolicyProvider extends PolicyProvider {
	
	/**
	 * Gets the authn policy by resource.
	 * 
	 * @param resourceName
	 *            the resource name
	 * @param operationName
	 *            the operation name
	 * @param resourceType
	 *            the resource type
	 * @return the authn policy by resource
	 * @throws PolicyProviderException
	 *             the policy provider exception
	 */
	AuthenticationProviderInfo getAuthnPolicyByResource(String resourceName, String operationName, String resourceType)
			throws org.ebayopensource.turmeric.utils.config.exceptions.PolicyProviderException;
}
