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
package com.nuodb.migrator.jdbc.metadata.filter;

import com.nuodb.migrator.jdbc.metadata.Identifiable;
import com.nuodb.migrator.jdbc.metadata.MetaDataType;
import com.nuodb.migrator.match.Regex;

import static com.nuodb.migrator.match.AntRegexCompiler.INSTANCE;

/**
 * @author Sergey Bushik
 */
public class MetaDataNameMatchesFilter<T extends Identifiable> extends MetaDataNameFilterBase<T> {

    private Regex regex;
    private boolean invertAccept;

    public MetaDataNameMatchesFilter(MetaDataType objectType, String regex,
                                    boolean invertAccept) {
        super(objectType, false);
        this.regex = INSTANCE.compile(regex);
        this.invertAccept = invertAccept;
    }

    public MetaDataNameMatchesFilter(MetaDataType objectType, boolean qualifyName,
                                    String regex, boolean invertAccept) {
        super(objectType, qualifyName);
        this.regex = INSTANCE.compile(regex);
        this.invertAccept = invertAccept;
    }

    @Override
    protected boolean accepts(String name) {
        boolean equals = name != null && regex.test(name);
        return !invertAccept ? equals : !equals;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        MetaDataNameMatchesFilter that = (MetaDataNameMatchesFilter) o;

        if (invertAccept != that.invertAccept) return false;
        if (regex != null ? !regex.equals(that.regex) : that.regex != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (regex != null ? regex.hashCode() : 0);
        result = 31 * result + (invertAccept ? 1 : 0);
        return result;
    }
}