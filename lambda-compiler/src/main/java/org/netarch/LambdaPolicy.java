/*
 * Copyright 2017-present Network Architecture Laboratory, Tsinghua University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.netarch;

public class LambdaPolicy {
    private LambdaPredicate predicate;
    private LambdaPath path;

    public LambdaPolicy() {
        predicate = null;
        path = null;
    }

    public LambdaPolicy(LambdaPredicate predicate, LambdaPath path) {
        this.predicate = predicate;
        this.path = path;
    }

    public LambdaPredicate getPredicate() {
        return predicate;
    }

    public LambdaPath getPath() {
        return path;
    }


    public void setPath(LambdaPath path) {
        this.path = path;
    }

    public void setPredicate(LambdaPredicate predicate) {
        this.predicate = predicate;
    }
}
