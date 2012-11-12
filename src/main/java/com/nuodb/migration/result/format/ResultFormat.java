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
package com.nuodb.migration.result.format;

import com.nuodb.migration.jdbc.metamodel.ColumnModelSet;
import com.nuodb.migration.jdbc.metamodel.ColumnModelSet;
import com.nuodb.migration.jdbc.type.JdbcType;
import com.nuodb.migration.jdbc.type.access.JdbcTypeValueAccess;
import com.nuodb.migration.result.format.jdbc.JdbcTypeValueFormat;

import java.util.Map;

/**
 * @author Sergey Bushik
 */
public interface ResultFormat {

    void initModel();

    String getType();

    void setAttributes(Map<String, String> attributes);

    Map<String, String> getAttributes();

    String getAttribute(String attribute);

    String getAttribute(String attribute, String defaultValue);

    JdbcTypeValueAccess getJdbcTypeValueAccess();

    void setJdbcTypeValueAccess(JdbcTypeValueAccess jdbcTypeValueAccess);

    void addJdbcTypeValueFormat(int typeCode, JdbcTypeValueFormat jdbcTypeValueFormat);

    void addJdbcTypeValueFormat(JdbcType jdbcType, JdbcTypeValueFormat jdbcTypeValueFormat);

    JdbcTypeValueFormat getJdbcTypeValueFormat(JdbcType jdbcType);

    JdbcTypeValueFormat getJdbcTypeValueFormat(int typeCode);

    JdbcTypeValueFormat getDefaultJdbcTypeValueFormat();

    void setDefaultJdbcTypeValueFormat(JdbcTypeValueFormat defaultJdbcTypeValueFormat);

    ColumnModelSet getColumnModelSet();

    void setColumnModelSet(ColumnModelSet columnModelSet);
}