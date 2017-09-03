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


public class LambdaCompiler implements LambdaCompilerService {
    @Override
    public LambdaPolicy compile(String statement) {
        String stmt = statement.trim();
        int idx = stmt.indexOf("->");
        if (idx < 0) {
            return null;
        }
        // policy::= predicate -> path
        String predicateStr = stmt.substring(0, idx);
        String pathStr = stmt.substring(idx+2);

        System.out.println(predicateStr);
        System.out.println(pathStr);

        LambdaPolicy policy = new LambdaPolicy();

        LambdaPredicate predicate = parsePredicate(predicateStr);
        policy.setPredicate(predicate);

        LambdaPath path = parsePath(pathStr);
        policy.setPath(path);

        return policy;
    }

    private String formalize(String str) {
        return null;
    }


    private LambdaPredicate parsePredicate(String predicateStr) {
        LambdaPredicate predicate = new LambdaPredicate();

        int openBracketIdx = predicateStr.indexOf('(');
        int closeBracketIdx = predicateStr.indexOf(')');
        if (openBracketIdx < 0 || closeBracketIdx < 0) {
            return null;
        }

        String tmpStr = predicateStr.substring(openBracketIdx + 1, closeBracketIdx);
        if (tmpStr.contains("and") || predicateStr.contains("or")) {
            String[] slist = tmpStr.replace("and", "&").replace("or", "&").split("&");
            for (String s : slist) {
                predicate.addAtomicPredicate(parseAtomicPredicate(s));
            }
        } else { // atomic predicate
            AtomicPredicate atomicPredicate = parseAtomicPredicate(predicateStr.substring(openBracketIdx, closeBracketIdx));
            predicate.addAtomicPredicate(atomicPredicate);
        }
        return predicate;
    }

    private AtomicPredicate parseAtomicPredicate(String atomicStr) {
        AtomicPredicate atomicPredicate = new AtomicPredicate();
        assert ( ! atomicStr.contains("and") && ! atomicStr.contains("or"));

        int dotIdx = atomicStr.indexOf('.');
        int equalIdx = atomicStr.indexOf('=');
        atomicPredicate.header = atomicStr.substring(0, dotIdx);
        atomicPredicate.field = atomicStr.substring(dotIdx+1, equalIdx);
        atomicPredicate.value = new FieldValue(atomicStr.substring(equalIdx+1).trim());
        return atomicPredicate;
    }

    private LambdaPath parsePath(String pathStr) {
        LambdaPath path = new LambdaPath();
        int curIndex = 0;
        LambdaNode node;
        while (curIndex < pathStr.length()) {
            switch (pathStr.charAt(curIndex)) {
                case ' ':
                    curIndex++;
                    continue;
                case '(':
                    int endIndex = pathStr.indexOf(')', curIndex);
                    if (endIndex < 0) {
                        return null;
                    }
                    node = parseNode(pathStr.substring(curIndex+1, endIndex));
                    path.addNode(node);
                    curIndex = endIndex + 1;
                    break;
                case '.':
                    curIndex++;
                    node = new LambdaNode();
                    node.setDpid(".");
                    while (curIndex < pathStr.length() && pathStr.charAt(curIndex) == ' ') {
                        curIndex++;
                    }
                    if (pathStr.charAt(curIndex) == '*') {
                        node.setRepeatable(true);
                    }
                    else {
                        node.setRepeatable(false);
                    }
                    curIndex++;
                    path.addNode(node);
                    break;
                default:
                    break;
            }

        }
        return null;
    }

    private LambdaNode parseNode(String nodeStr) {
        LambdaNode node = new LambdaNode();
        String[] slist = nodeStr.trim().split(" ");
        if (slist.length == 0) {
            return null;
        } else if (slist.length == 1) {
            node.setDpid(slist[0]);
        } else if (slist.length > 1) {
            node.setDpid(slist[0]);
            for (int i = 1; i < slist.length; i++) {
                try {
                    NetworkFeatureInstance instance = new NetworkFeatureInstance(NetworkFeature.getFeatureInstance(slist[i]));
                    node.getGraph().addInstance(instance);
                } catch (CompilerException e) {
                    System.err.print(e.toString());
                    continue;
                }
            }
        }

        return node;
    }

}

