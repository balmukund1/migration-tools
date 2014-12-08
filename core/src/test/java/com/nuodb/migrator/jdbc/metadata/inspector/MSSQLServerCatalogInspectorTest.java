package com.nuodb.migrator.jdbc.metadata.inspector;
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

import static com.google.common.collect.Iterables.get;
import static com.nuodb.migrator.jdbc.metadata.MetaDataType.CATALOG;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Collection;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.nuodb.migrator.jdbc.metadata.Catalog;

/**
 * @author Mukund
 */

public class MSSQLServerCatalogInspectorTest extends InspectorTestBase {

    public MSSQLServerCatalogInspectorTest() {
        super(MSSQLServerCatalogInspector.class);
    }

    @Override
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
    }

    @DataProvider(name = "getCatalogData")
    public Object[][] createGetCollationData() throws Exception{
        Catalog catalog = new Catalog();
        catalog.setName("test");
        catalog.setEncoding("latin1");

        return new Object[][] {
                { catalog } 
        };
    }

    @Test(dataProvider = "getCatalogData")
    public void testDatabaseCollation(Catalog catalog) throws Exception {
        configureCatalogCollationResultSet(catalog);
        configureCatalogInfo(catalog);
        InspectionResults inspectionResults = getInspectionManager().inspect(getConnection(), CATALOG);
        Collection<Catalog> catalogs = inspectionResults.getObjects(CATALOG);

        assertNotNull(catalogs);
        assertEquals(catalogs.size(), 1);
        assertEquals(get(catalogs, 0).getEncoding(), catalog.getEncoding());
    }

    private ResultSet configureCatalogCollationResultSet(Catalog catalog) throws Exception{
        PreparedStatement query = mock(PreparedStatement.class);
        given(getConnection().prepareStatement(anyString(), anyInt(), anyInt())).willReturn(query);
        given(getConnection().prepareStatement(anyString())).willReturn(query);

        ResultSet catResultSet = mock(ResultSet.class);
        given(query.executeQuery()).willReturn(catResultSet);
        given(catResultSet.next()).willReturn(true, false);
        given(catResultSet.getString(1)).willReturn("test");
        given(catResultSet.getString(2)).willReturn("SQL_latin1");
        return catResultSet;
    }

    private ResultSet configureCatalogInfo(Catalog catalog) throws Exception{
        ResultSet resultSet = mock(ResultSet.class);
        DatabaseMetaData dbmd = mock(DatabaseMetaData.class);

        ResultSetMetaData rsmd = mock(ResultSetMetaData.class);
        given(resultSet.getMetaData()).willReturn(rsmd);
        given(getConnection().getMetaData()).willReturn(dbmd);
        given(getConnection().getMetaData().getCatalogs()).willReturn(resultSet);
        given(resultSet.next()).willReturn(true, false);
        given(resultSet.getString("TABLE_CAT")).willReturn("test");
        return resultSet;
    }
}
