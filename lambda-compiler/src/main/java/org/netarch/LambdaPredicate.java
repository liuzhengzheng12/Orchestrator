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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public String getHeaderField() {
        return header+"."+field;
    }

}

public class LambdaPredicate {
    private List<AtomicPredicate> atomicPredicateList;
    private Map<String, AtomicPredicate> atomicPredicateMap;


    LambdaPredicate() {
        atomicPredicateList = new ArrayList<>();
        atomicPredicateMap = new HashMap<>();
    }

    public void addAtomicPredicate(AtomicPredicate atomicPredicate) throws LambdaCompilerException {
        if (atomicPredicate.getFiled() == null
                || atomicPredicate.getHeader() == null
                || atomicPredicate.getValue() == null) {
            throw new LambdaCompilerException("A wrong atomic predicate.");
        }

        atomicPredicateMap.put(atomicPredicate.getHeaderField(), atomicPredicate);

        atomicPredicateList.add(atomicPredicate);
    }

    public List<AtomicPredicate> getAtomicPredicateList() {
        return atomicPredicateList;
    }

    public AtomicPredicate getAtomicPredicate(String headerField) {
        return atomicPredicateMap.get(headerField);
    }

}
