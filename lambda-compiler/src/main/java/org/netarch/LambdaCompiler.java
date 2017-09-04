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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LambdaCompiler implements LambdaCompilerService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public LambdaPolicy compile(String statement) throws LambdaCompilerException {
        String stmt = statement.trim();
        String[] tmp = stmt.split("->");
        if (tmp.length < 2) {
            throw new LambdaCompilerException("Can not find the '->'");
        }

        // policy::= predicate -> path
        String predicateStr = tmp[0];
        String pathStr = tmp[1];

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
                try {
                    predicate.addAtomicPredicate(parseAtomicPredicate(s));
                } catch (LambdaCompilerException e) {
                    e.printStackTrace();
                    System.exit(1);
                }

            }
        } else { // atomic predicate
            AtomicPredicate atomicPredicate = parseAtomicPredicate(predicateStr.substring(openBracketIdx, closeBracketIdx));
            try {
                predicate.addAtomicPredicate(atomicPredicate);
            } catch (LambdaCompilerException e) {
                e.printStackTrace();
                System.exit(1);
            }

        }
        return predicate;
    }

    private AtomicPredicate parseAtomicPredicate(String atomicStr) {
        AtomicPredicate atomicPredicate = new AtomicPredicate();
        assert ( ! atomicStr.contains("and") && ! atomicStr.contains("or"));

        int dotIdx = atomicStr.indexOf('.');
        int equalIdx = atomicStr.indexOf('=');
        atomicPredicate.setHeader(atomicStr.substring(0, dotIdx));
        atomicPredicate.setFiled(atomicStr.substring(dotIdx+1, equalIdx));
        atomicPredicate.setValue(new FieldValue(atomicStr.substring(equalIdx+1).trim()));
        return atomicPredicate;
    }

    private LambdaPath parsePath(String pathStr) throws LambdaCompilerException {
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
                        throw new LambdaCompilerException(pathStr+" is not a valid path statement");
                    }
                    node = parseNode(pathStr.substring(curIndex+1, endIndex));
                    curIndex = endIndex + 1;

                    if (pathStr.charAt(curIndex) == '*') {
                        node.setRepeatable(true);
                        curIndex ++;
                    }
                    else {
                        node.setRepeatable(false);
                    }

                    path.addNode(node);
                    break;
                case '.':
                    curIndex++;
                    node = new LambdaNode();
                    node.setNullGraph();
                    if (pathStr.charAt(curIndex) == '*') {
                        node.setRepeatable(true);
                        curIndex++;
                    }
                    else {
                        node.setRepeatable(false);
                    }
                    path.addNode(node);
                    break;
                default:
                    throw new LambdaCompilerException(pathStr.charAt(curIndex) + " at "
                            + curIndex + " of " + pathStr +" is not a valid path statement");
            }

        }
        return path;
    }

    private LambdaNode parseNode(String nodeStr) throws LambdaCompilerException {
        LambdaNode node = new LambdaNode();
        node.setGraph(new NetworkFeatureGraph());

        String[] slist = nodeStr.trim().split(" ");
        if (slist.length == 0) {
            return null;
        }
        else {
            int i = 0;
            if (!NetworkFeature.containFeatureInstance(slist[0])) {
                node.setDpid(slist[0]);
                i = 1;
            }
            for (; i < slist.length; i++) {
                try {
                    NetworkFeatureInstance instance = new NetworkFeatureInstance(NetworkFeature.getFeatureInstance(slist[i]));
                    node.getGraph().addInstance(instance);
                } catch (LambdaCompilerException e) {
                    log.error(e.getMessage());
                }
            }
        }

        return node;
    }

}

