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


import org.netarch.utils.IndentPrintWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class AtomicPredicate {
    private String header;
    private String field;
    private FieldValue value;

    AtomicPredicate() {
        header = null;
        field = null;
        value = null;
    }

    /**
     *
     * @return
     */
    public String getFiled() {
        return field;
    }

    /**
     *
     * @return
     */
    public String getHeader() {
        return header;
    }

    /**
     *
     * @return
     */
    public FieldValue getValue() {
        return value;
    }

    /**
     *
     * @param field
     * @return
     */
    public AtomicPredicate setFiled(String field) {
        this.field = field.trim();
        return this;
    }

    /**
     *
     * @param header
     * @return
     */
    public AtomicPredicate setHeader(String header) {
        this.header = header.trim();
        return this;
    }

    /**
     *
     * @param value
     * @return
     */
    public AtomicPredicate setValue(FieldValue value) {
        this.value = value;
        return this;
    }

    public String getHeaderField() {
        return header+"."+field;
    }

    /**
     *
     * @param pw
     */
    public void printTo(IndentPrintWriter pw) {
        pw.incIndent();
        pw.println(this.header + "." + this.field + '=' + this.value);
        pw.decIndent();
    }

    @Override
    public String toString() {
        return this.header + "." + this.field + '=' + this.value;
    }
}

public class LambdaPredicate {
    private List<AtomicPredicate> atomicPredicateList;
    private Map<String, AtomicPredicate> atomicPredicateMap;

    /**
     *
     */
    LambdaPredicate() {
        atomicPredicateList = new ArrayList<>();
        atomicPredicateMap = new HashMap<>();
    }

    /**
     *
     * @param atomicPredicate
     * @throws LambdaCompilerException
     */
    public void addAtomicPredicate(AtomicPredicate atomicPredicate) throws LambdaCompilerException {
        if (atomicPredicate.getFiled() == null
                || atomicPredicate.getHeader() == null
                || atomicPredicate.getValue() == null) {
            throw new LambdaCompilerException("A wrong atomic predicate.");
        }

        atomicPredicateMap.put(atomicPredicate.getHeaderField(), atomicPredicate);

        atomicPredicateList.add(atomicPredicate);
    }

    /**
     *
     * @return
     */
    public List<AtomicPredicate> getAtomicPredicateList() {
        return atomicPredicateList;
    }

    /**
     *
     * @param headerField
     * @return
     */
    public AtomicPredicate getAtomicPredicate(String headerField) {
        return atomicPredicateMap.get(headerField);
    }

    /**
     *
     * @param pw
     */
    public void printTo(IndentPrintWriter pw) {
        pw.incIndent();
        pw.println("Predicate");
        for (AtomicPredicate atom : atomicPredicateList) {
            atom.printTo(pw);
        }
        pw.decIndent();
    }

    @Override
    public String toString() {
        String str = "Predicate\n";
        for (AtomicPredicate atom : atomicPredicateList) {
            str = str + "\t" + atom.toString() + "\n";
        }
        return str;
    }
}
