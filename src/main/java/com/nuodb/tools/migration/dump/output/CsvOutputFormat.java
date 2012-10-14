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
package com.nuodb.tools.migration.dump.output;

import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Sergey Bushik
 */
@SuppressWarnings("unchecked")
public class CsvOutputFormat extends OutputFormatBase {

    private CsvListWriter output;

    @Override
    public void init() {
        Writer writer = getWriter();
        OutputStream outputStream = getOutputStream();
        CsvPreference preference = CsvPreference.STANDARD_PREFERENCE;
        if (writer != null) {
            output = new CsvListWriter(writer, preference);
        } else if (outputStream != null) {
            output = new CsvListWriter(new PrintWriter(outputStream), preference);
        }
    }

    @Override
    protected void doOutputBegin(ResultSet resultSet) throws IOException, SQLException {
        output.writeHeader(getResultSetMetaModel().getColumns());
    }

    @Override
    protected void doOutputRow(ResultSet resultSet) throws IOException, SQLException {
        output.write(formatColumns(resultSet));
    }

    @Override
    protected void doOutputEnd(ResultSet resultSet) throws IOException, SQLException {
        output.flush();
    }
}
