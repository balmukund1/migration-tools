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


import com.nuodb.migrator.jdbc.metadata.Table;
import com.nuodb.migrator.jdbc.query.Query;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;

import com.nuodb.migrator.jdbc.metadata.Partition;
import com.nuodb.migrator.jdbc.query.ParameterizedQuery;

import static com.nuodb.migrator.jdbc.query.Queries.newQuery;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.nuodb.migrator.jdbc.metadata.inspector.InspectionResultsUtils.addTable;

/**
 * @author Mukund
 */
public class NuoDBPartitionInspector extends SimplePartitionInspector {

    private static final String QUERY = " SELECT COUNT(*), PC.SCHEMA, PC.TABLENAME, PC.PARTITIONNAME , PC.ISDEFAULT, PC.VALUE, PT.PARTITIONTYPE,"
            + "PT.PARTITIONINGCOLUMN, PT.DEFAULTPARTITION, PI.STORAGEGROUP FROM SYSTEM.PARTITIONCRITERIA PC "
            + "JOIN SYSTEM.PARTITIONEDTABLES PT ON  PC.TABLENAME=PT.TABLENAME AND PC.SCHEMA=PT.SCHEMA\n "
            + "JOIN  SYSTEM.PARTITIONIDS PI  ON PC.TABLENAME=PI.TABLENAME AND PC.SCHEMA=PI.SCHEMA AND PC.PARTITIONNAME=PI.PARTITIONNAME "
            + " WHERE PC.SCHEMA= ? AND PT.TABLENAME= ? GROUP BY PC.SCHEMA ,PC.TABLENAME, PC.PARTITIONNAME, PC.ISDEFAULT,"
            + "PC.VALUE,PT.PARTITIONTYPE, PT.PARTITIONINGCOLUMN, PT.DEFAULTPARTITION ,PI.STORAGEGROUP ORDER BY PC.PARTITIONNAME,PC.VALUE";

    public NuoDBPartitionInspector() {
        super();
    }

    @Override
    protected Query createQuery(InspectionContext inspectionContext, TableInspectionScope tableInspectionScope) {
        Collection<Object> parameters = newArrayList();
        parameters.add(tableInspectionScope.getSchema());
        parameters.add(tableInspectionScope.getTable());
        return new ParameterizedQuery(newQuery(QUERY), parameters);
    }

    @Override
    protected void processResultSet(InspectionContext inspectionContext, ResultSet partitions) throws SQLException {
        InspectionResults inspectionResults = inspectionContext.getInspectionResults();
        ArrayList<String> listPartition = new ArrayList<String>();
        Map<String, String> partitionMap = newLinkedHashMap();
        String value = "";
        int i = 0;
        while (partitions.next()) {
            Table table = addTable(inspectionResults, null, partitions.getString("SCHEMA"),
                    partitions.getString("TABLENAME"));
            Partition partition = table.addPartition(partitions.getString("PARTITIONNAME"));
            listPartition.add(partition.getName());

            partitionMap.put(partitions.getString("PARTITIONNAME"), partitions.getString("STORAGEGROUP"));
            partition.setPartitionType(partitions.getInt("PARTITIONTYPE"));
            partition.setPartitionMap(partitionMap);
            partition.setIsdefault(partitions.getInt("ISDEFAULT"));
            if (partitions.getInt("PARTITIONTYPE") == 1) {
                if (i != 0) {
                    if (partition.getName().equals(listPartition.get(i - 1))) {
                        value = value + isNumber(partitions.getString("VALUE")) + ",";
                    } else {
                        value = "";
                        value = value + isNumber(partitions.getString("VALUE")) + ",";
                    }
                } else {
                    value = value + isNumber(partitions.getString("VALUE")) + ",";
                }
            } else {
                value = partitions.getString("VALUE");
            }
            partition.setValue(value);
            partition.setDefaultpartition(partitions.getString("DEFAULTPARTITION"));
            partition.setPartitioningColumn(partitions.getString("PARTITIONINGCOLUMN"));
            table.setPartitionStatus(partitions.getInt("COUNT"));
            partition.setStorageGroup(partitions.getString("STORAGEGROUP"));
            
            inspectionResults.addObject(partition);
            i++;
        }
    }

    protected String isNumber(String num) {
        if (NumberUtils.isNumber(num)) {
            return num;
        } else {
            return "\'"+num+"\'";
        }
    }

    @Override
    protected boolean supportsScope(TableInspectionScope tableInspectionScope) {
        return tableInspectionScope.getSchema() != null
                && tableInspectionScope.getTable() != null;
    }
}
