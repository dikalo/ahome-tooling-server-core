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

import groovy.transform.CompileStatic
import groovy.transform.Memoized

import java.nio.ByteBuffer

import javax.websocket.PongMessage
import javax.websocket.Session

import com.ait.tooling.common.api.java.util.StringOps
import com.ait.tooling.server.core.support.CoreGroovySupport

@CompileStatic
public abstract class AbstractWebSocketEndPointByPathPart extends CoreGroovySupport
{
    private final String                    m_pathpart

    protected AbstractWebSocketEndPointByPathPart(final String pathpart)
    {
        m_pathpart = StringOps.toTrimOrNull(pathpart)
    }

    public void onOpen(final Session session)
    {
        final String name = getEndPointName(session)

        final IWebSocketService service = getWebSocketService(name)

        if (null != service)
        {
            getWebSocketServiceProvider().addEndPoint(session, name, service)
        }
        else
        {
            logger().error("onOpen(" + name + ") Can't find WebSocketService")
        }
    }

    public void onClose(final Session session)
    {
        getWebSocketServiceProvider().removeEndPoint(session, getEndPointName(session))
    }

    public void onText(final Session session, final String text, final boolean last)
    {
        final String name = getEndPointName(session)

        try
        {
            if (session.isOpen())
            {
                getWebSocketServiceProvider().onMessage(session, name, text, last)
            }
            else
            {
                logger().error("onText(" + name + ") Session is closed")
            }
        }
        catch (Exception e)
        {
            logger().error("onText(" + name + ")", e)

            if (doCloseOnException(e))
            {
                try
                {
                    session.close()
                }
                catch (Exception i)
                {
                }
            }
        }
    }

    public void onBinary(final Session session, final ByteBuffer bb, final boolean last)
    {
    }

    public void onPongMessage(final PongMessage pm)
    {
    }

    public String getEndPointName(final Session session)
    {
        getPathParameter(session, getPathPart())
    }

    public String getPathParameter(final Session session, final String name)
    {
        session.getPathParameters().get(name)
    }

    @Memoized
    public String getPathPart()
    {
        m_pathpart
    }

    public boolean doCloseOnException(Exception e)
    {
        true
    }
}