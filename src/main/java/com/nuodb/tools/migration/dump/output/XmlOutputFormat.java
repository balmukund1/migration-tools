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

import com.nuodb.tools.migration.jdbc.metamodel.ResultSetMetaModel;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;

/**
 * @author Sergey Bushik
 */
public class XmlOutputFormat extends OutputFormatBase {

    public static final String ENCODING = "utf-8";
    public static final String VERSION = "1.0";
    public static final String DOCUMENT_ELEMENT = "rows";
    public static final String ROW_ELEMENT = "row";

    private XMLStreamWriter writer;

    private String encoding = ENCODING;
    private String version = VERSION;
    private String documentElement = DOCUMENT_ELEMENT;
    private String rowElement = ROW_ELEMENT;

    @Override
    public void init() {
        XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
        Writer writer = getWriter();
        OutputStream outputStream = getOutputStream();
        try {
            if (writer != null) {
                this.writer = xmlOutputFactory.createXMLStreamWriter(writer);
            } else {
                this.writer = xmlOutputFactory.createXMLStreamWriter(outputStream, getEncoding());
            }
        } catch (XMLStreamException e) {
            throw new OutputFormatException(e);
        }
    }

    @Override
    protected void doOutputBegin(ResultSet resultSet) throws IOException, SQLException {
        try {
            writer.writeStartDocument(getEncoding(), getVersion());
            writer.writeStartElement(getDocumentElement());
            writer.setPrefix("xsi", W3C_XML_SCHEMA_INSTANCE_NS_URI);
        } catch (XMLStreamException e) {
            throw new OutputFormatException(e);
        }
    }

    @Override
    protected void doOutputRow(ResultSet resultSet) throws IOException, SQLException {
        try {
            writer.writeStartElement(getRowElement());
            List<String> columns = formatColumns(resultSet);
            for (int index = 0, columnsSize = columns.size(); index < columnsSize; index++) {
                doOutputColumn(columns.get(index), index);
            }
            writer.writeEndElement();
        } catch (XMLStreamException e) {
            throw new OutputFormatException(e);
        }
    }

    protected void doOutputColumn(String column, int index) throws XMLStreamException {
        ResultSetMetaModel metaModel = getResultSetMetaModel();
        String element = metaModel.getColumn(index);
        if (column == null) {
            writer.writeEmptyElement(element);
            writer.writeAttribute(W3C_XML_SCHEMA_INSTANCE_NS_URI, "nil", "true");
        } else {
            writer.writeStartElement(element);
            writer.writeCharacters(column);
            writer.writeEndElement();
        }
    }

    @Override
    protected void doOutputEnd(ResultSet resultSet) throws IOException, SQLException {
        try {
            writer.writeEndElement();
            writer.writeEndDocument();
            writer.flush();
        } catch (XMLStreamException e) {
            throw new OutputFormatException(e);
        }
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDocumentElement() {
        return documentElement;
    }

    public void setDocumentElement(String documentElement) {
        this.documentElement = documentElement;
    }

    public String getRowElement() {
        return rowElement;
    }

    public void setRowElement(String rowElement) {
        this.rowElement = rowElement;
    }
}
