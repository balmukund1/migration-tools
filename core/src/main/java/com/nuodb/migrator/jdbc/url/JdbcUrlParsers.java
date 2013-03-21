/**
 * Copyright (c) 2012, NuoDB, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of NuoDB, Inc. nor the names of its contributors may
 *       be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL NUODB, INC. BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.nuodb.migrator.jdbc.url;

import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @author Sergey Bushik
 */
public class JdbcUrlParsers {

    private static JdbcUrlParsers INSTANCE;

    public static JdbcUrlParsers getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new JdbcUrlParsers();
        }
        return INSTANCE;
    }

    private Collection<JdbcUrlParser> parsers = Sets.newLinkedHashSet();

    private JdbcUrlParsers() {
        addParser(new JTDSJdbcUrlParser());
        addParser(new MSSQLJdbcUrlParser());
        addParser(new MySQLJdbcUrlParser());
        addParser(new NuoDBJdbcUrlParser());
        addParser(new OracleJdbcUrlParser());
        addParser(new PostgreSQLJdbcUrlParser());
    }

    public boolean canParse(String url) {
        return getParser(url) != null;
    }

    public JdbcUrl parse(String url) {
        return parse(url, Collections.<String, Object>emptyMap());
    }

    public JdbcUrl parse(String url, Map<String, Object> overrides) {
        for (JdbcUrlParser parser : getParsers()) {
            if (parser.canParse(url)) {
                return parser.parse(url, overrides);
            }
        }
        return null;
    }

    public void addParser(JdbcUrlParser jdbcUrlParser) {
        getParsers().add(jdbcUrlParser);
    }

    public JdbcUrlParser getParser(String url) {
        for (JdbcUrlParser parser : getParsers()) {
            if (parser.canParse(url)) {
                return parser;
            }
        }
        return null;
    }

    public Collection<JdbcUrlParser> getParsers() {
        return parsers;
    }
}