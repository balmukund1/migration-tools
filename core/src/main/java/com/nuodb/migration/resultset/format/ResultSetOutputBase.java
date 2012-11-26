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

import com.nuodb.migration.jdbc.model.ColumnModel;
import com.nuodb.migration.jdbc.model.ColumnModelFactory;
import com.nuodb.migration.jdbc.model.ColumnModelSet;
import com.nuodb.migration.jdbc.type.JdbcTypeDesc;
import com.nuodb.migration.jdbc.type.access.JdbcTypeValueAccess;
import com.nuodb.migration.resultset.format.jdbc.JdbcTypeValueFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Sergey Bushik
 */
@SuppressWarnings("unchecked")
public abstract class ResultSetOutputBase extends ResultSetFormatBase implements ResultSetOutput {

    private transient final Logger logger = LoggerFactory.getLogger(getClass());

    private Writer writer;
    private OutputStream outputStream;
    private ResultSet resultSet;

    public Writer getWriter() {
        return writer;
    }

    @Override
    public void setWriter(Writer writer) {
        this.writer = writer;
        initOutput();
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
        initOutput();
    }

    protected abstract void initOutput();

    @Override
    public final void setResultSet(ResultSet resultSet) {
        this.resultSet = resultSet;
        initColumnValueModelSet();
    }

    protected void initColumnValueModelSet() {
        ColumnModelSet<ColumnModel> columnModelSet = getColumnModelSet();
        if (columnModelSet == null) {
            setColumnModelSet(createColumnSetModel());
        }
        ColumnModelSet<ColumnValueModel> columnValueModelSet = getColumnValueModelSet();
        if (columnValueModelSet == null) {
            setColumnValueModelSet(createColumnValueModelSet());
        }
    }

    protected ColumnModelSet<ColumnValueModel> createColumnValueModelSet() {
        int index = 0;
        final ColumnModelSet<ColumnValueModel> columnModelSet = ColumnModelFactory.createColumnModelSet();
        for (ColumnModel columnModel : getColumnModelSet()) {
            ColumnValueModel columnValueModel = createColumnValueModel(columnModel, index++);
            visitColumnValueModel(columnValueModel);
            columnModelSet.add(columnValueModel);
        }
        return columnModelSet;
    }

    protected ColumnModelSet<ColumnModel> createColumnSetModel() {
        ColumnModelSet columnModelSet;
        try {
            columnModelSet = ColumnModelFactory.createColumnModelSet(resultSet);
        } catch (SQLException exception) {
            throw new ResultSetOutputException(exception);
        }
        return columnModelSet;
    }

    protected ColumnValueModel createColumnValueModel(ColumnModel columnModel, int index) {
        JdbcTypeDesc jdbcTypeDesc = new JdbcTypeDesc(columnModel.getTypeCode(), columnModel.getTypeName());
        JdbcTypeValueFormat columnValueFormat =
                getJdbcTypeValueFormatRegistry().getJdbcTypeValueFormat(jdbcTypeDesc);
        JdbcTypeValueAccess<Object> columnValueAccess =
                getJdbcTypeValueAccessProvider().getResultSetAccess(resultSet, columnModel, index + 1);
        return new ColumnValueModelImpl(columnModel, columnValueFormat, columnValueAccess, null);
    }

    @Override
    public final void writeBegin() {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Write begin %s", getClass().getName()));
        }
        doWriteBegin();
    }

    protected abstract void doWriteBegin();

    @Override
    public final void writeRow() {
        writeColumnValues(getColumnValues());
    }

    protected String[] getColumnValues() {
        int index = 0;
        final ColumnModelSet<ColumnValueModel> columnValueModelSet = getColumnValueModelSet();
        final String[] values = new String[columnValueModelSet.size()];
        for (ColumnValueModel columnValueModel : columnValueModelSet) {
            values[index++] = columnValueModel.getValueFormat().getValue(
                    columnValueModel.getValueAccess(), columnValueModel.getValueAccessOptions());
        }
        return values;
    }

    protected abstract void writeColumnValues(String[] values);

    @Override
    public void writeEnd() {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("End result output %s", getClass().getName()));
        }
        doWriteEnd();
    }

    protected abstract void doWriteEnd();
}