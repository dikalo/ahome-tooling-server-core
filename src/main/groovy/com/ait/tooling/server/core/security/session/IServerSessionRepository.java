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

package com.ait.tooling.server.core.security.session;

import java.io.Serializable;
import java.util.Map;

import org.springframework.session.SessionRepository;

import com.ait.tooling.server.core.json.JSONObject;

public interface IServerSessionRepository extends SessionRepository<IServerSession>, Serializable
{
    public static final double MAX_RATE_LIMIT = 2000.0;

    public static final double MIN_RATE_LIMIT = 0.1000;

    public String getDomain();

    public boolean isActive();

    public Map<String, Object> getProperties();

    public Iterable<String> getRolesForUser(String user);

    public void setRateLimit(double limit);

    public void touch(IServerSession session);

    public IServerSession createSession(JSONObject keys);

    public void cleanExpiredSessions();
}
