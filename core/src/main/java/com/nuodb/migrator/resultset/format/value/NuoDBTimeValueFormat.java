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
package com.nuodb.migrator.resultset.format.value;

import com.nuodb.migrator.jdbc.model.ValueModel;
import com.nuodb.migrator.jdbc.type.access.JdbcTypeValueAccess;

import java.util.Map;

import static com.nuodb.migrator.resultset.format.value.ValueVariants.string;

/**
 * @author Sergey Bushik
 */
public class NuoDBTimeValueFormat extends ValueFormatBase<String> {
    @Override
    protected ValueVariant doGetValue(JdbcTypeValueAccess<String> valueAccess, Map<String, Object> valueAccessOptions) throws Exception {
        return string(valueAccess.getValue(String.class, valueAccessOptions));
    }

    @Override
    protected void doSetValue(ValueVariant variant, JdbcTypeValueAccess<String> valueAccess,
                              Map<String, Object> valueAccessOptions) throws Exception {
        String value = variant.asString();
        valueAccess.setValue(value, valueAccessOptions);
    }

    @Override
    public ValueVariantType getVariantType(ValueModel ValueModel) {
        return ValueVariantType.STRING;
    }
}