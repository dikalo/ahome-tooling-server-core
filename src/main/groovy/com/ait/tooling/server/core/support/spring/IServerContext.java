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

package com.ait.tooling.server.core.support.spring;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

import com.ait.tooling.json.JSONArray;
import com.ait.tooling.json.JSONObject;
import com.ait.tooling.json.parser.JSONParserException;
import com.ait.tooling.json.schema.JSONSchema;
import com.ait.tooling.server.core.jmx.management.ICoreServerManager;
import com.ait.tooling.server.core.security.IAuthorizationProvider;
import com.ait.tooling.server.core.security.IAuthorizer;
import com.ait.tooling.server.core.security.ICryptoProvider;

public interface IServerContext extends IAuthorizer, IPropertiesResolver, Serializable
{
    public IServerContext getServerContext();

    public ApplicationContext getApplicationContext();

    public boolean containsBean(String name);

    public <B> B getBean(String name, Class<B> type) throws Exception;

    public <B> B getBeanSafely(String name, Class<B> type);

    public Environment getEnvironment();

    public IAuthorizationProvider getAuthorizationProvider();

    public Iterable<String> getPrincipalsKeys();

    public ICoreServerManager getCoreServerManager();

    public IBuildDescriptorProvider getBuildDescriptorProvider();

    public IPropertiesResolver getPropertiesResolver();

    public ICryptoProvider getCryptoProvider();

    public MessageChannel getMessageChannel(String name);
    
    public PublishSubscribeChannel getPublishSubscribeChannel(String name);
    
    public SubscribableChannel getSubscribableChannel(String name);

    public <T> boolean publish(String name, Message<T> message);

    public <T> boolean publish(String name, Message<T> message, long timeout);

    public Logger logger();

    public JSONObject json();

    public JSONObject json(Map<String, ?> valu);

    public JSONObject json(List<?> list);

    public JSONObject json(String name, Object value);

    public JSONObject json(Collection<?> collection);

    public JSONArray jarr();

    public JSONArray jarr(JSONObject object);

    public JSONArray jarr(List<?> list);

    public JSONArray jarr(Map<String, ?> valu);

    public JSONArray jarr(String name, Object value);

    public JSONArray jarr(Collection<?> collection);

    public JSONSchema jsonSchema(Map<String, ?> schema);

    public JSONObject jsonParse(String string) throws JSONParserException;

    public JSONObject jsonParse(Reader reader) throws IOException, JSONParserException;

    public String uuid();
}