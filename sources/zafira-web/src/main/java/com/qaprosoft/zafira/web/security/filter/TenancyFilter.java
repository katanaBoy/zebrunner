/*******************************************************************************
 * Copyright 2013-2019 Qaprosoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.web.security.filter;

import com.google.common.net.InternetDomainName;
import com.qaprosoft.zafira.dbaccess.utils.TenancyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;

/**
 * TenancyFilter - retrieves tenant by subdomain.
 * 
 * @author akhursevich
 */
@Component
public class TenancyFilter extends GenericFilterBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenancyFilter.class);

    private static final String[] EXCLUSIONS = {"api/status"};

    @Value("${zafira.multitenant}")
    private boolean isMultitenant;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;

        if (Arrays.stream(EXCLUSIONS).noneMatch(path -> servletRequest.getRequestURI().contains(path))) {
            String tenantName = servletRequest.getHeader("Tenant");
            if (isMultitenant) {
                TenancyContext.setTenantName(tenantName);
            }
        }

        chain.doFilter(request, response);
    }
}
