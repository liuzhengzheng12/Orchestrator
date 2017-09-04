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
import java.util.List;

public class LambdaPath {
    public static final LambdaPath NULL_PATH = new LambdaPath();

    private List<LambdaNode> nodeList;

    public LambdaPath() {
        this.nodeList = new ArrayList<>();
    }

    public void addNode(LambdaNode node) {
        nodeList.add(node);
    }

    public List<LambdaNode> getNodeList() {
        return nodeList;
    }

    public void printTo(IndentPrintWriter pw) {
        pw.incIndent();
        for (LambdaNode node : nodeList) {
            node.printTo(pw);
        }
        pw.decIndent();
    }

    @Override
    public String toString() {
        String str = "Path\n";
        for (LambdaNode node : nodeList) {
            str += "\t" + node.toString() + "\n";
        }
        return str;
    }
}
