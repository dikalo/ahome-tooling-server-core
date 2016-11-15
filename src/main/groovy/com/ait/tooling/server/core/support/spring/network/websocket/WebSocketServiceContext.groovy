/*
 * Copyright (c) 2014,2015,2016 Ahome' Innovation Technologies. All rights reserved.
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

package com.ait.tooling.server.core.support.spring.network.websocket

import javax.websocket.Session

import com.ait.tooling.common.api.java.util.StringOps
import com.ait.tooling.server.core.json.JSONArray
import com.ait.tooling.server.core.json.JSONObject

import groovy.transform.CompileStatic
import groovy.transform.Memoized

@CompileStatic
public class WebSocketServiceContext implements IWebSocketServiceContext
{
    private boolean             m_istrict

    private Session             m_session

    private JSONObject          m_attribs

    private IWebSocketService   m_service

    public WebSocketServiceContext()
    {
        this(new JSONObject())
    }

    public WebSocketServiceContext(final JSONObject attr)
    {
        m_attribs = Objects.requireNonNull(attr)
    }

    public final void setSession(final Session session)
    {
        if (null != m_session)
        {
            m_session = Objects.requireNonNull(session)

            m_attribs.merge(m_session.getPathParameters())
        }
        else
        {
            throw new IllegalArgumentException('WebSocket Session already set')
        }
    }

    public final void setStrict(final boolean strict)
    {
        m_istrict = strict
    }

    public final void setService(final IWebSocketService service)
    {
        if (null != m_service)
        {
            m_service = Objects.requireNonNull(service)

            m_attribs.merge(m_service.getAttributes())
        }
        else
        {
            throw new IllegalArgumentException('IWebSocketService already set')
        }
    }

    @Override
    public IWebSocketService getService()
    {
        m_service
    }

    @Override
    public void close() throws IOException
    {
        final Session session = getSession()

        if (session.isOpen())
        {
            session.close()
        }
    }

    @Override
    public Session getSession()
    {
        m_session
    }

    @Override
    public boolean isOpen()
    {
        getSession().isOpen()
    }

    @Memoized
    public String getPathParameter(final String name)
    {
        getPathParameters().get(StringOps.requireTrimOrNull(name))
    }

    @Memoized
    public Map<String, String> getPathParameters()
    {
        getSession().getPathParameters()
    }

    @Override
    public boolean isStrict()
    {
        m_istrict
    }

    @Override
    public void reply(final String text) throws Exception
    {
        reply(Objects.requireNonNull(text), true)
    }

    @Override
    public void reply(final String text, final boolean last) throws Exception
    {
        final Session sess = getSession()

        synchronized (sess)
        {
            sess.getBasicRemote().sendText(Objects.requireNonNull(text), last)
        }
    }

    @Override
    public void reply(final JSONObject json) throws Exception
    {
        reply(json.toJSONString(isStrict()))
    }

    @Override
    public void reply(final JSONArray batch) throws Exception
    {
        reply(batch.toJSONString(isStrict()))
    }

    @Override
    public void batch(final JSONObject json) throws Exception
    {
        reply(json.toJSONString(isStrict()))
    }

    @Memoized
    public String getId()
    {
        getSession().getId()
    }

    @Override
    public JSONObject getAttributes()
    {
        m_attribs
    }
}