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

public class LambdaNode {
    private boolean repeatable;
    private String dpid;
    private NetworkFeatureGraph graph;

    public LambdaNode() {
        repeatable = false;
        dpid = null;
        graph = null;
    }

    public LambdaNode(String dpid) {
        this.dpid = dpid;
        this.graph = null;
        this.repeatable = false;
    }

    public LambdaNode setNullGraph() {
        graph = NetworkFeatureGraph.NULL_GRAPH;
        return this;
    }

    public NetworkFeatureGraph getGraph() {
        return graph;
    }

    public boolean isRepeatable() {
        return repeatable;
    }

    public String getDpid() {
        return dpid;
    }

    public LambdaNode setDpid(String dpid) {
        this.dpid = dpid;
        return this;
    }

    public LambdaNode setGraph(NetworkFeatureGraph graph) {
        if (graph != null) {
            this.repeatable = true;
        }
        this.graph = graph;
        return this;
    }

    public LambdaNode setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
        return this;
    }

    public boolean isNullGraph() {
        return graph == NetworkFeatureGraph.NULL_GRAPH;
    }
}
