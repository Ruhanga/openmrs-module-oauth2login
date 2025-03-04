/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.oauth2login.web.filter;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class OAuth2LoginRequestFilterTest {
	
	private OAuth2LoginRequestFilter filter = new OAuth2LoginRequestFilter();
	
	@Before
	public void setup() throws ServletException {
		PowerMockito.spy(Context.class);
		
		FilterConfig filterConfig = mock(FilterConfig.class);
		when(filterConfig.getInitParameter(eq("moduleURIs"))).thenReturn("/oauth2login");
		filter.init(filterConfig);
	}
	
	@Test
	public void defaultLogoutUri_shouldRedirectToLogoutUri() throws Exception {
		// setup
		MockHttpServletRequest request = new MockHttpServletRequest();
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		
		// replay
		request.setServletPath("/logout");
		filter.doFilter(request, response, chain);
		
		// verify
		verify(response, times(1)).sendRedirect("/oauth2logout");
		verifyZeroInteractions(chain);
	}
	
	@Test
	public void logoutUri_shouldRedirectToLoginUri() throws Exception {
		// setup
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getContextPath()).thenReturn("");
		when(request.getServletPath()).thenReturn("/oauth2logout");
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		
		OAuth2LoginRequestFilter filterSpy = spy(filter);
		
		// replay
		filterSpy.doFilter(request, response, chain);
		
		// verify
		{
			PowerMockito.verifyStatic(times(1));
			Context.logout();
		}
		verify(filterSpy, times(1)).logoutFromSpringSecurity(request, response);
		verify(response, times(1)).sendRedirect("/oauth2login");
		verifyZeroInteractions(chain);
	}
	
	@Test
	public void secureUri_shouldRedirectToLoginUriWhenNotAuthenticated() throws Exception {
		// setup
		MockHttpServletRequest request = new MockHttpServletRequest();
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		
		Context.setUserContext(new UserContext(null));
		when(Context.isAuthenticated()).thenReturn(false);
		
		// replay
		request.setServletPath("/getPatient");
		filter.doFilter(request, response, chain);
		
		// verify
		verify(response, times(1)).sendRedirect("/oauth2login");
		verifyZeroInteractions(chain);
	}
	
	@Test
	public void moduleHandledUri_shouldProceedWhenNotAuthenticated() throws Exception {
		// setup
		MockHttpServletRequest request = new MockHttpServletRequest();
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		
		Context.setUserContext(new UserContext(null));
		when(Context.isAuthenticated()).thenReturn(false);
		
		// replay
		request.setServletPath("/oauth2login");
		filter.doFilter(request, response, chain);
		
		// verify
		verify(response, never()).sendRedirect(any(String.class));
		verify(chain, times(1)).doFilter(request, response);
	}
	
	@Test
	public void secureUri_shouldProceedWhenAuthenticated() throws Exception {
		// setup
		MockHttpServletRequest request = new MockHttpServletRequest();
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		
		Context.setUserContext(new UserContext(null));
		when(Context.isAuthenticated()).thenReturn(true);
		
		// replay
		request.setServletPath("/getPatient");
		filter.doFilter(request, response, chain);
		
		// verify
		verify(response, never()).sendRedirect(any(String.class));
		verify(chain, times(1)).doFilter(request, response);
	}
}
