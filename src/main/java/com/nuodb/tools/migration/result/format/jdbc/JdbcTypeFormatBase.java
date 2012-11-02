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
package com.nuodb.tools.migration.result.format.jdbc;

import com.nuodb.tools.migration.jdbc.type.JdbcType;
import com.nuodb.tools.migration.result.format.ResultFormatException;

/**
 * @author Sergey Bushik
 */
public abstract class JdbcTypeFormatBase<T> implements JdbcTypeFormat<T> {

    @Override
    public String format(JdbcTypeValue<T> jdbcTypeValue) {
        try {
            return doFormat(jdbcTypeValue);
        } catch (Exception exception) {
            throw newResultFormatFailure(jdbcTypeValue.getJdbcType(), exception);
        }
    }

    protected abstract String doFormat(JdbcTypeValue<T> jdbcTypeValue) throws Exception;

    @Override
    public void parse(JdbcTypeValue<T> jdbcTypeValue, String value) {
        try {
            doParse(jdbcTypeValue, value);
        } catch (Exception exception) {
            throw newResultFormatFailure(jdbcTypeValue.getJdbcType(), exception);
        }
    }

    protected abstract void doParse(JdbcTypeValue<T> jdbcTypeValue, String value) throws Exception;

    protected ResultFormatException newResultFormatFailure(JdbcType jdbcType, Exception exception) {
        return new ResultFormatException(
                String.format("Failed processing jdbc type %s", jdbcType.getClass().getName()), exception);
    }
}