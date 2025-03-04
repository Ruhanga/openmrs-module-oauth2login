/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.oauth2login;

public class OAuth2LoginConstants {
	
	/*
	 * Module ids
	 */
	public static final String MODULE_NAME = "OAuth 2.0 Login";
	
	public static final String MODULE_ARTIFACT_ID = "oauth2login";
	
	public static final String MODULE_SHORT_ID = "OAuth2";
	
	public static final String MODULE_BASE_URL = "/" + MODULE_ARTIFACT_ID;
	
	public static final String COMPONENT_LEGACY_CONTROLLER = MODULE_ARTIFACT_ID + "." + MODULE_NAME + "Controller";
	
}
