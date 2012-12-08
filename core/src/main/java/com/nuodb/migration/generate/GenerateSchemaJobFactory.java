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
package com.nuodb.migration.generate;

import com.google.common.collect.Maps;
import com.nuodb.migration.jdbc.connection.ConnectionProviderFactory;
import com.nuodb.migration.jdbc.dialect.NuoDBDialect;
import com.nuodb.migration.jdbc.metadata.generator.ScriptGeneratorContext;
import com.nuodb.migration.jdbc.metadata.generator.SimpleScriptGeneratorContext;
import com.nuodb.migration.job.JobExecutor;
import com.nuodb.migration.job.JobExecutors;
import com.nuodb.migration.job.JobFactory;
import com.nuodb.migration.job.TraceJobExecutionListener;
import com.nuodb.migration.spec.ConnectionSpec;
import com.nuodb.migration.spec.DriverConnectionSpec;
import com.nuodb.migration.spec.GenerateSchemaSpec;
import com.nuodb.migration.spec.ResourceSpec;

import static com.nuodb.migration.utils.ValidationUtils.isNotNull;

/**
 * @author Sergey Bushik
 */
public class GenerateSchemaJobFactory extends ConnectionProviderFactory implements JobFactory<GenerateSchemaJob> {

    private ScriptGeneratorContext scriptGeneratorContext = new SimpleScriptGeneratorContext();
    private GenerateSchemaSpec generateSchemaSpec;

    @Override
    public GenerateSchemaJob createJob() {
        isNotNull(generateSchemaSpec, "Generate schema spec is required");

        GenerateSchemaJob job = new GenerateSchemaJob();
        job.setOutputSpec(getGenerateSchemaSpec().getOutputSpec());
        job.setScriptGeneratorContext(createScriptGeneratorContext());
        ConnectionSpec sourceConnectionSpec = generateSchemaSpec.getSourceConnectionSpec();
        job.setSourceConnectionProvider(createConnectionProvider(sourceConnectionSpec, false));
        ConnectionSpec targetConnectionSpec = generateSchemaSpec.getTargetConnectionSpec();
        job.setTargetConnectionProvider(createConnectionProvider(targetConnectionSpec, false));
        return job;
    }

    protected ScriptGeneratorContext createScriptGeneratorContext() {
        scriptGeneratorContext.setDialect(new NuoDBDialect());
        ConnectionSpec targetConnectionSpec = getGenerateSchemaSpec().getTargetConnectionSpec();
        if (targetConnectionSpec != null) {
            scriptGeneratorContext.setCatalog(targetConnectionSpec.getCatalog());
            scriptGeneratorContext.setSchema(targetConnectionSpec.getSchema());
        }
        return scriptGeneratorContext;
    }

    public ScriptGeneratorContext getScriptGeneratorContext() {
        return scriptGeneratorContext;
    }

    public void setScriptGeneratorContext(ScriptGeneratorContext scriptGeneratorContext) {
        this.scriptGeneratorContext = scriptGeneratorContext;
    }

    public GenerateSchemaSpec getGenerateSchemaSpec() {
        return generateSchemaSpec;
    }

    public void setGenerateSchemaSpec(GenerateSchemaSpec generateSchemaSpec) {
        this.generateSchemaSpec = generateSchemaSpec;
    }

    public static void main(String[] args) {
        GenerateSchemaJobFactory jobFactory = new GenerateSchemaJobFactory();
        jobFactory.setGenerateSchemaSpec(new GenerateSchemaSpec() {
            {
                DriverConnectionSpec sourceConnectionSpec = new DriverConnectionSpec();
                sourceConnectionSpec.setDriverClassName("com.mysql.jdbc.Driver");
                sourceConnectionSpec.setUrl("jdbc:mysql://localhost:3306/mysql");
                sourceConnectionSpec.setUsername("root");

                DriverConnectionSpec targetConnectionSpec = new DriverConnectionSpec();
                targetConnectionSpec.setDriverClassName("com.nuodb.jdbc.Driver");
                targetConnectionSpec.setUrl("jdbc:com.nuodb://localhost/test");
                targetConnectionSpec.setUsername("dba");
                targetConnectionSpec.setPassword("goalie");
                targetConnectionSpec.setSchema("hockey");

                ResourceSpec outputSpec = new ResourceSpec();
                outputSpec.setPath("/tmp/test/schema.sql");

                //setOutputSpec(outputSpec);
                setSourceConnectionSpec(sourceConnectionSpec);
                //setTargetConnectionSpec(targetConnectionSpec);
            }
        });
        JobExecutor executor = JobExecutors.createJobExecutor(jobFactory.createJob());
        executor.addJobExecutionListener(new TraceJobExecutionListener());
        executor.execute(Maps.<String, Object>newHashMap());
    }
}