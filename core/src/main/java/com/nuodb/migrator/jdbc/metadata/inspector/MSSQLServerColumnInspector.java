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
package com.nuodb.migrator.jdbc.metadata.inspector;

import com.nuodb.migrator.jdbc.metadata.Column;
import com.nuodb.migrator.jdbc.metadata.DefaultValue;
import com.nuodb.migrator.jdbc.query.SelectQuery;
import com.nuodb.migrator.jdbc.query.StatementAction;
import com.nuodb.migrator.jdbc.query.StatementFactory;
import com.nuodb.migrator.jdbc.query.StatementTemplate;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.nuodb.migrator.jdbc.metadata.DefaultValue.valueOf;
import static org.apache.commons.lang3.StringUtils.endsWith;
import static org.apache.commons.lang3.StringUtils.startsWith;
import static com.nuodb.migrator.jdbc.metadata.inspector.MSSQLServerDatabaseInspector.formatEncoding;

/**
 * @author Sergey Bushik
 */
public class MSSQLServerColumnInspector extends SimpleColumnInspector {

    @Override
    protected void processColumn(InspectionContext inspectionContext, ResultSet columns, Column column) throws SQLException {
        super.processColumn(inspectionContext, columns, column);
        DefaultValue defaultValue = column.getDefaultValue();
        if (defaultValue != null && !defaultValue.isProcessed()) {
            String value = defaultValue.getScript();
            while (startsWith(value, "(") && endsWith(value, ")")) {
                value = value.substring(1, value.length() - 1);
            }
            column.setDefaultValue(valueOf(value, true));
        }

        String encoding = formatEncoding(getColumnCollation(inspectionContext, column.getName()));
        column.setEncoding(encoding != null ? encoding :null);
        try {
            if (encoding != null) {
                Charset colloation = Charset.forName(encoding);
                column.setCharset(colloation);
            }
        } catch (UnsupportedCharsetException unsupportedCharsetException) {
            unsupportedCharsetException.printStackTrace();
        }
    }

    /** 
    * This method is overridden to build a query to fetch column collation information from MSSQLSERVER 
    * sys.columns  table 
    */
    @Override
    protected String getColumnCollation(InspectionContext inspectionContext, final String column)
            throws SQLException {
            StatementTemplate template = new StatementTemplate(inspectionContext.getConnection());
            String columnCollation = template.executeStatement(
                    new StatementFactory<PreparedStatement>() {
                        @Override
                        public PreparedStatement createStatement(Connection connection) throws SQLException {
                            SelectQuery collationsQuery = new SelectQuery();
                            collationsQuery.column("name");
                            collationsQuery.column("collation_name");
                            collationsQuery.from("sys.columns");
                            collationsQuery.where("name = ?");
                            return connection.prepareStatement(collationsQuery.toString());
                        }
                    }, new StatementAction<PreparedStatement, String>() {
                        @Override
                        public String executeStatement(PreparedStatement statement) throws SQLException {
                            if(column != null) {
                                statement.setString(1, column);
                                }

                            ResultSet collations = statement.executeQuery();
                            String collation = null;
                            if (collations.next()) {
                                collation = collations.getString(2);
                            }
                            return collation;
                        }
                    }
            );
        return columnCollation;
    }
}
