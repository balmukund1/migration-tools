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
package com.nuodb.migration.jdbc.metadata.inspector;

import com.nuodb.migration.jdbc.metadata.Identifiable;
import com.nuodb.migration.jdbc.metadata.Identifier;
import com.nuodb.migration.jdbc.metadata.MetaData;
import com.nuodb.migration.jdbc.metadata.MetaDataType;

import java.util.Collection;

/**
 * @author Sergey Bushik
 */
public class RootInspectionResultsDelta implements InspectionResults {

    private InspectionResults rootInspectionResults;
    private InspectionResults deltaInspectionResults;

    public RootInspectionResultsDelta(InspectionResults rootInspectionResults) {
        this(rootInspectionResults, new SimpleInspectionResults());
    }

    public RootInspectionResultsDelta(InspectionResults rootInspectionResults,
                                      InspectionResults deltaInspectionResults) {
        this.rootInspectionResults = rootInspectionResults;
        this.deltaInspectionResults = deltaInspectionResults;
    }

    @Override
    public void addObject(MetaData object) {
        rootInspectionResults.addObject(object);
        deltaInspectionResults.addObject(object);
    }

    @Override
    public void addObjects(Collection<? extends MetaData> objects) {
        rootInspectionResults.addObjects(objects);
        deltaInspectionResults.addObjects(objects);
    }

    @Override
    public <M extends MetaData> M getObject(MetaDataType objectType) {
        return rootInspectionResults.getObject(objectType);
    }

    @Override
    public <M extends Identifiable> M getObject(MetaDataType objectType, String name) {
        return rootInspectionResults.getObject(objectType, name);
    }

    @Override
    public <M extends Identifiable> M getObject(MetaDataType objectType, Identifier identifier) {
        return rootInspectionResults.getObject(objectType, identifier);
    }

    @Override
    public <M extends MetaData> Collection<M> getObjects(MetaDataType objectType) {
        return rootInspectionResults.getObjects(objectType);
    }

    @Override
    public void removeObject(MetaData object) {
        rootInspectionResults.removeObject(object);
        deltaInspectionResults.removeObject(object);
    }

    @Override
    public Collection<? extends MetaData> getObjects() {
        return rootInspectionResults.getObjects();
    }

    public InspectionResults getRootInspectionResults() {
        return rootInspectionResults;
    }

    public InspectionResults getDeltaInspectionResults() {
        return deltaInspectionResults;
    }
}
