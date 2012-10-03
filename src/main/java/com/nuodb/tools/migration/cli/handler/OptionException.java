/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nuodb.tools.migration.cli.handler;

import com.nuodb.tools.migration.MigrationException;

/**
 * A problem found while dealing with executable line options.
 */
public class OptionException extends MigrationException {

    private Option option;

    public OptionException(Option option, String message) {
        super(message);
        this.option = option;
    }

    /**
     * Gets the Option the exception relates to
     *
     * @return The related Option
     */
    public Option getOption() {
        return option;
    }
}