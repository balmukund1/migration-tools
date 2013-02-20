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
package com.nuodb.migration.cli.parse.option;

import com.google.common.collect.Lists;
import com.nuodb.migration.cli.parse.Argument;
import com.nuodb.migration.cli.parse.BasicOption;
import com.nuodb.migration.cli.parse.CommandLine;
import com.nuodb.migration.cli.parse.Group;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.ListIterator;

import static com.nuodb.migration.cli.parse.option.OptionUtils.createArguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.*;

/**
 * @author Sergey Bushik
 */
public class GroupTest {

    private Group group;

    @BeforeMethod
    public void init() {
        group = spy(new GroupImpl());
    }

    @Test
    public void testTriggers() {
        BasicOption option = spy(new BasicOptionImpl());
        option.setName("option");
        group.addOption(option);

        assertNotNull(group.findOption("--option"));

        assertEquals(group.getPrefixes(), option.getPrefixes());
        assertEquals(group.getTriggers(), option.getTriggers());
    }

    @Test
    public void testRequired() {
        BasicOption option = spy(new BasicOptionImpl());
        group.addOption(option);
        group.setMinimum(1);

        assertTrue(group.isRequired(), "Minimum value is specified and group is required");
    }

    @Test
    public void testDefaults() {
        Argument argument = spy(new ArgumentImpl());
        List<Object> defaultValues = Lists.<Object>newArrayList("default1", "default2");
        argument.setDefaultValues(defaultValues);
        group.addOption(argument);

        CommandLine commandLine = mock(CommandLine.class);
        group.defaults(commandLine);

        verify(argument).defaults(commandLine);
        verify(commandLine).setDefaultValues(argument, defaultValues);
    }

    @Test
    public void testCanProcess() {
        BasicOption option = spy(new BasicOptionImpl());
        option.setName("option");
        group.addOption(option);

        Argument argument = spy(new ArgumentImpl());
        group.addOption(argument);

        CommandLine commandLine = mock(CommandLine.class);

        ListIterator<String> arguments = createArguments("--option", "argument");
        assertTrue(group.canProcess(commandLine, arguments),
                "Group should be able to process the argument by triggering underlying option");

        arguments.next();
        assertTrue(group.canProcess(commandLine, arguments),
                "Argument should be able to process current command line option");
    }
}