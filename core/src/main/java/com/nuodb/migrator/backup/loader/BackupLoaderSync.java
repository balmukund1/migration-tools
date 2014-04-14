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
package com.nuodb.migrator.backup.loader;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Sergey Bushik
 */
public class BackupLoaderSync {

    private boolean failed;
    private final AtomicBoolean loadData;
    private final AtomicBoolean loadSchemaNoIndexes;
    private final AtomicBoolean loadSchemaIndexes;
    private final CountDownLatch permits;

    public BackupLoaderSync(boolean loadData, boolean loadSchema) {
        this(loadData, loadSchema, loadSchema);
    }

    public BackupLoaderSync(boolean loadData, boolean loadSchemaNoIndexes, boolean loadSchemaIndexes) {
        this.loadData = new AtomicBoolean(loadData);
        this.loadSchemaIndexes = new AtomicBoolean(loadSchemaIndexes);
        this.loadSchemaNoIndexes = new AtomicBoolean(loadSchemaNoIndexes);
        int permits = 0;
        if (loadData) {
            permits++;
        }
        if (loadSchemaNoIndexes) {
            permits++;
        }
        if (loadSchemaIndexes) {
            permits++;
        }
        this.permits = new CountDownLatch(permits);
    }

    public void loadFailed() {
        failed = true;
        loadDataDone();
        loadSchemaIndexesDone();
        loadSchemaNoIndexesDone();
    }

    public void loadDataDone() {
        if (loadData.compareAndSet(true, false)) {
            permits.countDown();
        }
    }

    public void loadSchemaIndexesDone() {
        if (loadSchemaIndexes.compareAndSet(true, false)) {
            permits.countDown();
        }
    }

    public void loadSchemaNoIndexesDone() {
        if (loadSchemaNoIndexes.compareAndSet(true, false)) {
            permits.countDown();
        }
    }

    public boolean isFailed() {
        return failed;
    }

    public void await() throws InterruptedException {
        permits.await();
    }
}
