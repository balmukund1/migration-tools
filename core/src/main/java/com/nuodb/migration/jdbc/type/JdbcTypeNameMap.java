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
package com.nuodb.migration.jdbc.type;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Types;
import java.util.Map;

/**
 * @author Sergey Bushik
 */
public class JdbcTypeNameMap {

    private static final Map<Integer, String> TYPE_CODE_NAMES = Maps.newHashMap();

    private static final Logger logger = LoggerFactory.getLogger(JdbcTypeNameMap.class);

    static {
        Field[] fields = Types.class.getFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) && field.getType() == int.class) {
                try {
                    TYPE_CODE_NAMES.put((Integer) field.get(null), field.getName());
                } catch (IllegalAccessException exception) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Failed accessing jdbc type code field", exception);
                    }
                }
            }
        }
    }

    public static final JdbcTypeNameMap INSTANCE = new JdbcTypeNameMap();

    private Map<Integer, String> typeCodeNameMap = Maps.newHashMap();

    public JdbcTypeNameMap() {
        this.typeCodeNameMap = TYPE_CODE_NAMES;
    }

    public String getTypeName(int typeCode) {
        String typeName = typeCodeNameMap.get(typeCode);
        return typeName == null ? getUnknownTypeName(typeCode) : typeName;
    }

    public void addTypeCodeName(int typeCode, String typeName) {
        typeCodeNameMap.put(typeCode, typeName);
    }

    protected String getUnknownTypeName(int typeCode) {
        return "TYPE:" + typeCode;
    }
}
