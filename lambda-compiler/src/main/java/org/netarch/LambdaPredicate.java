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


import java.util.ArrayList;
import java.util.List;

class AtomicPredicate {
    String header;
    String field;
    FieldValue value;

    AtomicPredicate() {
        header = null;
        field = null;
        value = null;
    }

    public String getFiled() {
        return field;
    }

    public String getHeader() {
        return header;
    }

    public FieldValue getValue() {
        return value;
    }

    public AtomicPredicate setFiled(String field) {
        this.field = field;
        return this;
    }

    public AtomicPredicate setHeader(String header) {
        this.header = header;
        return this;
    }

    public AtomicPredicate setValue(FieldValue value) {
        this.value = value;
        return this;
    }
}

public class LambdaPredicate {
    protected List<AtomicPredicate> atomicPredicateList;

    LambdaPredicate() {
        atomicPredicateList = new ArrayList<>();
    }

    public void addAtomicPredicate(AtomicPredicate atomicPredicate) {
        atomicPredicateList.add(atomicPredicate);
    }

    public List<AtomicPredicate> getAtomicPredicateList() {
        return atomicPredicateList;
    }

}
