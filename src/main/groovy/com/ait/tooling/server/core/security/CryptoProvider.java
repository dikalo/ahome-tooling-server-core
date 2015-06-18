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

package com.ait.tooling.server.core.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import com.ait.tooling.common.api.hash.Hasher;
import com.ait.tooling.common.api.java.util.IHTTPConstants;

public final class CryptoProvider implements ICryptoProvider
{
    private static final long           serialVersionUID = -685446946283005169L;

    private static final Logger         logger           = Logger.getLogger(CryptoProvider.class);

    private static final String         HMAC_ALGORITHM   = "HmacSHA256";

    private final TextEncryptor         m_pcrypt;

    private final BCryptPasswordEncoder m_bcrypt;

    private final Hasher                m_hasher;

    private final SecretKeySpec         m_secret;

    private CryptoProvider(final String pass, final String salt)
    {
        m_pcrypt = Encryptors.text(Objects.requireNonNull(pass), Objects.requireNonNull(salt));

        m_bcrypt = new BCryptPasswordEncoder();

        m_hasher = new Hasher(this);

        try
        {
            m_secret = new SecretKeySpec(pass.getBytes(IHTTPConstants.CHARSET_UTF_8), HMAC_ALGORITHM);
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public final synchronized String makeSignature(final String text)
    {
        return hmac(Objects.requireNonNull(text));
    }

    @Override
    public final synchronized boolean testSignature(final String text, final String value)
    {
        Objects.requireNonNull(text);

        return value.equals(hmac(text));
    }

    private final String hmac(final String text)
    {
        try
        {
            final Mac hmac = Mac.getInstance(HMAC_ALGORITHM);

            hmac.init(m_secret);

            return Hex.encodeHexString(hmac.doFinal(text.getBytes(IHTTPConstants.CHARSET_UTF_8)));
        }
        catch (Exception e)
        {
            logger.error("hmac error", e);

            return null;
        }
    }

    @Override
    public final synchronized String makeBCrypt(final String text)
    {
        return m_bcrypt.encode(Objects.requireNonNull(text));
    }

    @Override
    public final String encrypt(final String text)
    {
        return m_pcrypt.encrypt(Objects.requireNonNull(text));
    }

    @Override
    public final String decrypt(final String text)
    {
        return m_pcrypt.decrypt(Objects.requireNonNull(text));
    }

    @Override
    public final synchronized boolean testBCrypt(final String text, final String value)
    {
        return m_bcrypt.matches(Objects.requireNonNull(text), Objects.requireNonNull(value));
    }

    @Override
    public String sha512(final String text, final String salt)
    {
        return m_hasher.sha512(Objects.requireNonNull(text), Objects.requireNonNull(salt));
    }

    @Override
    public String sha512(final String text, final String salt, final int iter)
    {
        return m_hasher.sha512(Objects.requireNonNull(text), Objects.requireNonNull(salt), iter);
    }

    @Override
    public String sha512(final String text)
    {
        Objects.requireNonNull(text);

        MessageDigest md;

        try
        {
            md = MessageDigest.getInstance("SHA-512");
        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("No SHA-512 Algorithm ", e);

            throw new IllegalArgumentException(e);
        }
        byte[] bytes;

        try
        {
            bytes = text.getBytes(IHTTPConstants.CHARSET_UTF_8);
        }
        catch (UnsupportedEncodingException e)
        {
            logger.error("No " + IHTTPConstants.CHARSET_UTF_8 + " encoding ", e);

            bytes = text.getBytes();
        }
        md.update(bytes);

        return Hex.encodeHexString(md.digest());
    }

    @Override
    public void close()
    {
    }
}
