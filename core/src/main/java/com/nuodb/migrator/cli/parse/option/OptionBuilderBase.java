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
package com.nuodb.migrator.cli.parse.option;

import com.nuodb.migrator.cli.parse.Option;
import com.nuodb.migrator.cli.parse.OptionProcessor;
import com.nuodb.migrator.cli.parse.OptionValidator;
import com.nuodb.migrator.cli.parse.Trigger;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static com.nuodb.migrator.utils.ReflectionUtils.newInstance;

/**
 * @author Sergey Bushik
 */
public abstract class OptionBuilderBase<O extends Option> implements OptionBuilder<O> {

    private Class<? extends O> optionClass;

    protected int id;
    protected String name;
    protected String description;
    protected boolean required;

    protected OptionFormat optionFormat;
    protected Set<OptionValidator> optionValidators = newHashSet();
    protected Set<OptionProcessor> optionProcessors = newHashSet();
    protected Set<Trigger> triggers = newHashSet();

    public OptionBuilderBase(Class<? extends O> optionClass, OptionFormat optionFormat) {
        this.optionClass = optionClass;
        this.optionFormat = optionFormat;
    }

    @Override
    public OptionBuilder withId(int id) {
        this.id = id;
        return this;
    }

    @Override
    public OptionBuilder withName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public OptionBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public OptionBuilder withRequired(boolean required) {
        this.required = required;
        return this;
    }

    @Override
    public OptionBuilder withOptionValidator(OptionValidator optionValidator) {
        this.optionValidators.add(optionValidator);
        return this;
    }

    @Override
    public OptionBuilder withOptionProcessor(OptionProcessor optionProcessor) {
        this.optionProcessors.add(optionProcessor);
        return this;
    }

    @Override
    public OptionBuilder withOptionFormat(OptionFormat optionFormat) {
        this.optionFormat = optionFormat;
        return this;
    }

    @Override
    public OptionBuilder withTrigger(Trigger trigger) {
        this.triggers.add(trigger);
        return this;
    }

    @Override
    public O build() {
        O option = create();
        option.setId(id);
        option.setName(name);
        option.setDescription(description);
        option.setRequired(required);
        option.setOptionFormat(optionFormat);
        for (Trigger trigger : triggers) {
            option.addTrigger(trigger);
        }
        for (OptionValidator optionValidator : optionValidators) {
            option.addOptionValidator(optionValidator);
        }
        for (OptionProcessor optionProcessor : optionProcessors) {
            option.addOptionProcessor(optionProcessor);
        }
        return option;
    }

    protected O create() {
        return newInstance(optionClass);
    }
}