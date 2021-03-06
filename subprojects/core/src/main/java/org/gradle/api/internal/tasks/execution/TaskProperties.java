/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.internal.tasks.execution;

import com.google.common.collect.ImmutableSortedSet;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.tasks.TaskInputFilePropertySpec;
import org.gradle.api.internal.tasks.TaskOutputFilePropertySpec;
import org.gradle.api.internal.tasks.TaskValidationContext;
import org.gradle.internal.Factory;

import java.util.Map;

public interface TaskProperties {

    Factory<Map<String, Object>> getInputPropertyValues();

    ImmutableSortedSet<TaskInputFilePropertySpec> getInputFileProperties();

    FileCollection getInputFiles();

    boolean hasSourceFiles();

    FileCollection getSourceFiles();

    ImmutableSortedSet<TaskOutputFilePropertySpec> getOutputFileProperties();

    FileCollection getOutputFiles();

    boolean hasDeclaredOutputs();

    FileCollection getLocalStateFiles();

    FileCollection getDestroyableFiles();

    void validate(TaskValidationContext validationContext);
}
