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
package com.nuodb.migration.resultset.format;

import com.google.common.collect.Maps;
import com.nuodb.migration.jdbc.model.ColumnModel;
import com.nuodb.migration.jdbc.model.ColumnModelSet;
import com.nuodb.migration.jdbc.type.access.JdbcTypeValueAccessProvider;
import com.nuodb.migration.jdbc.type.jdbc2.JdbcDateTypeBase;
import com.nuodb.migration.resultset.format.jdbc.JdbcTypeValueFormatRegistry;

import java.sql.Types;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author Sergey Bushik
 */
public abstract class ResultSetFormatBase implements ResultSetFormat {

    private Map<String, String> attributes;
    private TimeZone timeZone;
    private ColumnModelSet<ColumnModel> columnModelSet;
    private ColumnModelSet<ColumnValueModel> columnValueModelSet;
    private JdbcTypeValueAccessProvider jdbcTypeValueAccessProvider;
    private JdbcTypeValueFormatRegistry jdbcTypeValueFormatRegistry;

    @Override
    public final void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getAttribute(String attribute) {
        return attributes != null ? attributes.get(attribute) : null;
    }

    @Override
    public String getAttribute(String attribute, String defaultValue) {
        String value = null;
        if (attributes != null) {
            value = attributes.get(attribute);
        }
        return value == null ? defaultValue : value;
    }

    protected void visitColumnValueModel(ColumnValueModel columnValueModel) {
        int typeCode = columnValueModel.getTypeCode();
        if (typeCode == Types.TIME || typeCode == Types.TIMESTAMP || typeCode == Types.DATE) {
            TimeZone timeZone = getTimeZone();
            if (timeZone != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeZone(timeZone);
                Map<String, Object> options = Maps.newHashMap();
                options.put(JdbcDateTypeBase.CALENDAR, calendar);
                columnValueModel.setValueAccessOptions(options);
            }
        }
    }

    @Override
    public Map<String, String> getAttributes() {
        return attributes;
    }

    @Override
    public TimeZone getTimeZone() {
        return timeZone;
    }

    @Override
    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public JdbcTypeValueAccessProvider getJdbcTypeValueAccessProvider() {
        return jdbcTypeValueAccessProvider;
    }

    @Override
    public void setJdbcTypeValueAccessProvider(JdbcTypeValueAccessProvider jdbcTypeValueAccessProvider) {
        this.jdbcTypeValueAccessProvider = jdbcTypeValueAccessProvider;
    }

    @Override
    public JdbcTypeValueFormatRegistry getJdbcTypeValueFormatRegistry() {
        return jdbcTypeValueFormatRegistry;
    }

    @Override
    public void setJdbcTypeValueFormatRegistry(JdbcTypeValueFormatRegistry jdbcTypeValueFormatRegistry) {
        this.jdbcTypeValueFormatRegistry = jdbcTypeValueFormatRegistry;
    }

    @Override
    public ColumnModelSet<ColumnModel> getColumnModelSet() {
        return columnModelSet;
    }

    @Override
    public void setColumnModelSet(ColumnModelSet<ColumnModel> columnModelSet) {
        this.columnModelSet = columnModelSet;
    }

    @Override
    public ColumnModelSet<ColumnValueModel> getColumnValueModelSet() {
        return columnValueModelSet;
    }

    @Override
    public void setColumnValueModelSet(ColumnModelSet<ColumnValueModel> columnValueModelSet) {
        this.columnValueModelSet = columnValueModelSet;
    }
}