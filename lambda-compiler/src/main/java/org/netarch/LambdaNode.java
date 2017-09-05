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

/**
 * A node in a network feature graph
 */
public class LambdaNode {
    private boolean repeatable;
    private String dpid;
    private NetworkFeatureGraph graph;

    /**
     *
     */
    public LambdaNode() {
        repeatable = false;
        dpid = null;
        graph = NetworkFeatureGraph.NULL_GRAPH;
    }

    /**
     *
     * @param dpid Data path ID
     */
    public LambdaNode(String dpid) {
        this.dpid = dpid;
        this.graph = NetworkFeatureGraph.NULL_GRAPH;
        this.repeatable = false;
    }

    /**
     * Set the node to has a NULL graph.
     * @return this
     */
    public LambdaNode setNullGraph() {
        graph = NetworkFeatureGraph.NULL_GRAPH;
        return this;
    }

    /**
     * Get the graph.
     * @return True if it is '.', or ''
     */
    public NetworkFeatureGraph getGraph() {
        return graph;
    }

    /**
     *
     * @return
     */
    public boolean isRepeatable() {
        return repeatable;
    }

    /**
     *
     * @return
     */
    public String getDpid() {
        return dpid;
    }

    /**
     * Set DPID
     * @param dpid
     * @return
     */
    public LambdaNode setDpid(String dpid) {
        this.dpid = dpid;
        return this;
    }

    /**
     * Set up the graph.
     * @param graph
     * @return
     */
    public LambdaNode setGraph(NetworkFeatureGraph graph) {
        if (graph != null) {
            this.repeatable = true;
        }
        this.graph = graph;
        return this;
    }

    /**
     * Set the repeat feature.
     * @param repeatable
     * @return
     */
    public LambdaNode setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
        return this;
    }

    /**
     * Whether the graph is '.' .
     * @return True if it is '.', or ''
     */
    public boolean isNullGraph() {
        return graph == NetworkFeatureGraph.NULL_GRAPH;
    }

    @Override
    public String toString() {
        String ret = "Node (";
        if(repeatable) {
            ret +="R";
        }
        if(isNullGraph()) {
            ret += "N";
        }
        ret +=")";


        if (dpid != null) {
            ret += " ["+dpid+"]";
        }

        if(!isNullGraph()) {
            ret += " ";
            ret += graph.toString();
        }

        return ret;
    }

    public void printTo(IndentPrintWriter pw) {
        pw.println(dpid);
    }


}
