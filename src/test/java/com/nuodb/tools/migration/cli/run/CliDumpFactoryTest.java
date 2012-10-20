package com.nuodb.tools.migration.cli.run;


import com.nuodb.tools.migration.cli.parse.Argument;
import com.nuodb.tools.migration.cli.parse.Option;
import com.nuodb.tools.migration.cli.parse.option.ArgumentBuilder;
import com.nuodb.tools.migration.cli.parse.option.GroupBuilder;
import com.nuodb.tools.migration.cli.parse.option.OptionBuilder;
import com.nuodb.tools.migration.cli.parse.option.OptionToolkit;
import org.junit.Before;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CliDumpFactoryTest {

    private OptionToolkit optionToolkit;

    @Before
    public void setUp() throws Exception {
        optionToolkit = mock(OptionToolkit.class);
        ArgumentBuilder argumentBuilder = mock(ArgumentBuilder.class);
        GroupBuilder groupBuilder = mock(GroupBuilder.class);
        OptionBuilder optionBuilder = mock(OptionBuilder.class);

        when(optionToolkit.newOption()).thenReturn(optionBuilder);
        when(optionBuilder.withId(anyInt())).thenReturn(optionBuilder);
        when(optionBuilder.withName(anyString())).thenReturn(optionBuilder);
        when(optionBuilder.withDescription(anyString())).thenReturn(optionBuilder);
        when(optionBuilder.withRequired(anyBoolean())).thenReturn(optionBuilder);
        when(optionBuilder.withAlias(anyString())).thenReturn(optionBuilder);
        when(optionBuilder.withArgument(any(Argument.class))).thenReturn(optionBuilder);

        when(optionToolkit.newArgument()).thenReturn(argumentBuilder);
        when(argumentBuilder.withId(anyInt())).thenReturn(argumentBuilder);
        when(argumentBuilder.withMaximum(anyInt())).thenReturn(argumentBuilder);
        when(argumentBuilder.withMinimum(anyInt())).thenReturn(argumentBuilder);
        when(argumentBuilder.withName(anyString())).thenReturn(argumentBuilder);
        when(argumentBuilder.withRequired(anyBoolean())).thenReturn(argumentBuilder);

        when(optionToolkit.newGroup()).thenReturn(groupBuilder);
        when(groupBuilder.withId(anyInt())).thenReturn(groupBuilder);
        when(groupBuilder.withMinimum(anyInt())).thenReturn(groupBuilder);
        when(groupBuilder.withOption(any(Option.class))).thenReturn(groupBuilder);
        when(groupBuilder.withName(anyString())).thenReturn(groupBuilder);
        when(groupBuilder.withRequired(anyBoolean())).thenReturn(groupBuilder);
    }
}
