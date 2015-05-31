/*
 * Copyright (c) 2014,2015 Ahome' Innovation Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ait.tooling.server.core.servlet.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ait.tooling.common.api.java.util.StringOps;
import com.ait.tooling.server.core.servlet.filter.AbstractHTTPFilter;

public class StrictTransportFilter extends AbstractHTTPFilter
{
    private static final long   serialVersionUID         = -4578897779521715272L;

    private static final Logger logger                   = Logger.getLogger(StrictTransportFilter.class);

    private boolean             m_force_strict_transport = false;

    public StrictTransportFilter()
    {
        logger.info("StrictTransportFilter()");
    }

    @Override
    public void doFilter(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException, ServletException
    {
        if (doForceStrictTransport() || request.isSecure())
        {
            response.setHeader(STRICT_TRANSPORT_SECURITY_HEADER, "max-age=" + YEAR_IN_SECONDS);
        }
        chain.doFilter(request, response);
    }

    protected boolean doForceStrictTransport()
    {
        return m_force_strict_transport;
    }

    @Override
    public void init(final FilterConfig config) throws ServletException
    {
        final String valu = StringOps.toTrimOrNull(config.getInitParameter("force-strict-transport"));

        if (null != valu)
        {
            m_force_strict_transport = Boolean.parseBoolean(valu);

            logger.info("StrictTransportFilter()init() force_strict_transport=[" + valu + "," + m_force_strict_transport + "]");
        }
    }
}