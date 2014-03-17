/**
 * Copyright (c) 2014, NuoDB, Inc.
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
package com.nuodb.migrator.jdbc.metadata.generator;

import com.nuodb.migrator.jdbc.metadata.Column;
import com.nuodb.migrator.jdbc.metadata.Schema;
import com.nuodb.migrator.jdbc.metadata.Sequence;

import java.util.Collection;

import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.indexOf;
import static com.nuodb.migrator.utils.Predicates.equalTo;

/**
 * @author Sergey Bushik
 */
public class SequenceQualifyNamingStrategy extends IdentifiableNamingStrategy<Sequence> {

    private static final String PREFIX = "SEQ";

    public SequenceQualifyNamingStrategy() {
        super(Sequence.class, PREFIX);
    }

    @Override
    protected String getNonPrefixedName(Sequence sequence, ScriptGeneratorManager scriptGeneratorManager) {
        StringBuilder nonPrefixedName = new StringBuilder();
        Collection<Column> columns = sequence.getColumns();
        if (columns.size() == 1) {
            Column column = get(columns, 0);
            nonPrefixedName.append(scriptGeneratorManager.getName(column.getTable(), false));
            nonPrefixedName.append(getDelimiter());
            nonPrefixedName.append(scriptGeneratorManager.getName(column, false));
        } else {
            Schema schema = sequence.getSchema();
            if (scriptGeneratorManager.getName(schema, false) != null) {
                nonPrefixedName.append(scriptGeneratorManager.getName(schema, false));
            } else {
                nonPrefixedName.append(scriptGeneratorManager.getName(schema.getCatalog(), false));
            }
            nonPrefixedName.append(getDelimiter());
            nonPrefixedName.append(indexOf(schema.getSequences(), equalTo(sequence)));
        }
        return nonPrefixedName.toString();
    }
}
