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
package com.nuodb.migrator.cli.run;

import com.google.common.collect.Maps;
import com.nuodb.migrator.cli.parse.Option;
import com.nuodb.migrator.cli.parse.OptionSet;
import com.nuodb.migrator.cli.parse.option.GroupBuilder;
import com.nuodb.migrator.cli.parse.option.OptionFormat;
import com.nuodb.migrator.jdbc.query.InsertType;
import com.nuodb.migrator.load.LoadJobFactory;
import com.nuodb.migrator.spec.LoadSpec;

import java.util.Map;

import static com.nuodb.migrator.utils.Priority.LOW;

/**
 * @author Sergey Bushik
 */
public class CliLoadJob extends CliRunJob {

    public static final String COMMAND = "load";

    public CliLoadJob() {
        super(COMMAND, new LoadJobFactory());
    }

    @Override
    protected Option createOption() {
        return newGroupBuilder()
                .withName(getResources().getMessage(LOAD_GROUP_NAME))
                .withOption(createTargetGroup())
                .withOption(createInputGroup())
                .withOption(createInsertTypeGroup())
                .withOption(createTimeZoneOption())
                .withRequired(true).build();
    }

    @Override
    protected void bind(OptionSet optionSet) {
        LoadSpec loadSpec = new LoadSpec();
        loadSpec.setConnectionSpec(parseTargetGroup(optionSet, this));
        loadSpec.setInputSpec(parseInputGroup(optionSet, this));
        loadSpec.setTimeZone(parseTimeZoneOption(optionSet, this));
        parseInsertTypeGroup(optionSet, loadSpec);
        ((LoadJobFactory) getJobFactory()).setLoadSpec(loadSpec);
    }

    protected Option createInsertTypeGroup() {
        GroupBuilder group = newGroupBuilder().withName(getMessage(INSERT_TYPE_GROUP_NAME));

        Option replace = newBasicOptionBuilder().
                withName(REPLACE_OPTION).
                withAlias(REPLACE_SHORT_OPTION, OptionFormat.SHORT).
                withDescription(getMessage(REPLACE_OPTION_DESCRIPTION)).build();
        group.withOption(replace);

        Option replaceType = newRegexOptionBuilder().
                withName(TABLE_REPLACE_OPTION).
                withDescription(getMessage(TABLE_REPLACE_OPTION_DESCRIPTION)).
                withRegex(TABLE_REPLACE_OPTION, 1, LOW).build();
        group.withOption(replaceType);

        Option insertType = newRegexOptionBuilder().
                withName(TABLE_INSERT_OPTION).
                withDescription(getMessage(TABLE_INSERT_OPTION_DESCRIPTION)).
                withRegex(TABLE_INSERT_OPTION, 1, LOW).build();
        group.withOption(insertType);

        return group.build();
    }

    protected void parseInsertTypeGroup(OptionSet optionSet, LoadSpec loadSpec) {
        loadSpec.setInsertType(optionSet.hasOption(REPLACE_OPTION) ? InsertType.REPLACE : InsertType.INSERT);
        Map<String, InsertType> tableInsertTypes = Maps.newHashMap();
        for (String table : optionSet.<String>getValues(TABLE_INSERT_OPTION)) {
            tableInsertTypes.put(table, InsertType.INSERT);
        }
        for (String table : optionSet.<String>getValues(TABLE_REPLACE_OPTION)) {
            tableInsertTypes.put(table, InsertType.REPLACE);
        }
        loadSpec.setTableInsertTypes(tableInsertTypes);
    }
}