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
package com.nuodb.migrator.jdbc.metadata;


import java.util.Collection;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.google.common.collect.Maps.newTreeMap;
import static com.nuodb.migrator.jdbc.metadata.Identifier.valueOf;
import static com.nuodb.migrator.jdbc.metadata.MetaDataType.PARTITION;
import static java.lang.String.format;

/**
 * @author Mukund
 */
public class Partition extends IdentifiableBase {

    private Table table;
    private int  isDefault;
    private String partitionName ;
    private String value;
    private String  partitionType ;
    private String defaultPartition;
    private String partitionColumn;
    private String storageGroup ;
    private int position;

    public Map<String, Partition> partitions = newTreeMap();
    public Map<String, String> partitionMap = newLinkedHashMap();

    public Partition() {
        super(PARTITION);
    }

    public Partition(String name) {
        this(valueOf(name));
    }

    public Partition(Identifier identifier) {
        super(PARTITION, identifier);
    }

    public String getPartitionName() {
        return partitionName;
    }

    public void setPartitonName(String PartitionName) {
        this.partitionName = PartitionName;
    }

    public String getValue() {
        return value;
    }

    public void setValue (String value) {
        if (isDefault == 1 & partitionType =="RANGE") {
            value="MAXVALUE";
        }
        else if (isDefault == 1 & partitionType == "LIST") {
            value="DEFAULT";
        }
        this.value = value;
        }

    public String getDefaultpartition() {
        return defaultPartition;
    }

    public void setDefaultpartition(String defaultpartition) {
        this.defaultPartition= defaultpartition;
    }

   public String  getPartitioningColumn()  {
       return partitionColumn;
   }

   public void setPartitioningColumn(String partitioningColumn) {
       this.partitionColumn= partitioningColumn;
   }

   public  Collection<Partition> getPartitions() {
       return newArrayList(partitions.values());
   }

   public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        if (this.table != null) {
           this.table.getPartitions().remove(this);
        }
        this.table = table;
    }

   public String getPartitionType() {
        return partitionType;
    }

    public void setPartitionType(int pType) {
        if(pType == 1) {
            partitionType= "LIST";
        }else if (pType == 3) {
            partitionType = "RANGE";
        }
        if(pType == 5) {
            partitionType="DEFAULTPARTITION" ; 
        }
    }

    public String getStorageGroup() {
        return storageGroup;
    }

    public void setStorageGroup(String storageGroup) {
        this.storageGroup = storageGroup;
    }

    public int getIsdefault() {
        return isDefault;
    }

    public void setIsdefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Map<String, String> getPartitionMap() {
        return partitionMap;
    }

    public void setPartitionMap(Map<String, String> partitionMap) {
        this.partitionMap = partitionMap;
    }

     @Override
    public boolean equals(Object o) {
        if (this == o) return false;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Partition partition = (Partition) o ;
        
        if (table != null ? !table.equals(partition.table) : partition.table != null) return false;
        if (isDefault !=partition.isDefault)return false;
        if (partitionName != null ? !partitionName.equals(partition.partitionName) : partition.partitionName != null) return false;
        if (partitionType != null ? !partitionType.equals(partition.partitionType) : partition.partitionType != null) return false;
        if (value != null ? !value.equals(partition.value) : partition.value != null) return false;
        if (defaultPartition != null ? !defaultPartition.equals(partition.defaultPartition) : partition.defaultPartition != null) return false;
        if (storageGroup != null ? !storageGroup .equals(partition.storageGroup ) : partition.storageGroup != null) return false;
        if (partitionColumn  != null ? !partitionColumn .equals(partition.partitionColumn ) : partition.partitionColumn != null) return false;
        return true;
    } 

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (table != null ? table.hashCode() : 0);
        result = 31 * result + (partitionName != null ? partitionName.hashCode() : 0);
        result = 31 * result + (partitionType != null ? partitionType.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (defaultPartition != null ? defaultPartition.hashCode() : 0);
        result = 31 * result + (storageGroup != null ? storageGroup.hashCode() : 0);
        result = 31 * result + (partitionColumn != null ? partitionColumn.hashCode() : 0);
        return result;
    }

    @Override
    public void output(int indent, StringBuilder buffer) {
        super.output(indent, buffer);
        buffer.append(' ');
        Collection<String> attributes = newArrayList();
        if (partitionName != null) {
            attributes.add(format("partitionName=%s",partitionName));
        }
        buffer.append(format(", partitionType=%s", partitionType));
        buffer.append(format(", isDefault=%d", isDefault));
        buffer.append(format(", value=%s", value));
        buffer.append(format(", defaultPartition=%s", defaultPartition));
        buffer.append(format(", partitionColumn=%s", partitionColumn));
        buffer.append(format(", position=%d", position));
        buffer.append(format(", storageGroup=%s", storageGroup));
    }
}
