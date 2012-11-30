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
package com.nuodb.migration.jdbc.metadata;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author Sergey Bushik
 */
public class IndentedOutputBase implements IndentedOutput {

    public static final String NEW_LINE = System.getProperty("line.separator");

    public static final int INDENT = 3;

    @Override
    public String output() {
        return output(0);
    }

    @Override
    public String output(int indent) {
        StringBuilder value = new StringBuilder();
        output(indent, value);
        return value.toString();
    }

    @Override
    public void output(StringBuilder buffer) {
        output(0, buffer);
    }

    @Override
    public void output(int indent, StringBuilder buffer) {
        output(indent, buffer, getIdentity());
    }

    protected void output(int indent, StringBuilder buffer, String value) {
        outputIndent(indent, buffer);
        buffer.append(value);
    }

    protected void output(int indent, StringBuilder buffer, Collection<? extends IndentedOutput> items) {
        buffer.append("[");
        outputNewLine(buffer);
        for (Iterator<? extends IndentedOutput> iterator = items.iterator(); iterator.hasNext(); ) {
            IndentedOutput item = iterator.next();
            item.output(indent + INDENT, buffer);
            if (iterator.hasNext()) {
                buffer.append(',');
            }
            outputNewLine(buffer);
        }
        output(indent, buffer, "]");
    }

    protected void outputNewLine(StringBuilder buffer) {
        buffer.append(NEW_LINE);
    }

    protected void outputIndent(int indent, StringBuilder buffer) {
        for (int index = 0; index < indent; index++) {
            buffer.append(' ');
        }
    }

    protected String getIdentity() {
        return getClass().getName() + "@" + System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return output();
    }
}