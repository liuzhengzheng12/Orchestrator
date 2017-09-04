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

import java.util.*;

class ConditionalExpression {
    public static ConditionalExpression TRUE = new ConditionalExpression();
    private String exp;
    private ConditionalExpression() {
        exp = null;
    }

    public ConditionalExpression(String exp) throws LambdaCompilerException {
        this.exp = exp;
        if (this.exp == null) {
            throw new LambdaCompilerException("Conditional expresson can not be null");
        }
    }
}

class NetworkFeatureInstance {
    private static int INSTANCE_COUNTER = 0;
    public final static NetworkFeatureInstance NULL_INSTANCE = new NetworkFeatureInstance();

    private NetworkFeature feature;
    private int instanceId;
    private Map<ConditionalExpression, NetworkFeatureInstance> nextFeatureInstanceMap;


    private NetworkFeatureInstance() {
        this.feature = null;
        this.instanceId = INSTANCE_COUNTER;
        INSTANCE_COUNTER += 1;
    }

    NetworkFeatureInstance(NetworkFeature feature) throws LambdaCompilerException {
        this.instanceId = INSTANCE_COUNTER;
        this.feature = feature;

        if (this.feature == null) {
            throw new LambdaCompilerException("Cannot create an instance with a null network feature.\n");
        }

        this.nextFeatureInstanceMap = new HashMap<>();

        INSTANCE_COUNTER += 1;
    }

    public int getInstanceId() {
        return this.instanceId;
    }

    @Override
    public int hashCode() {
        return instanceId;
    }

    public String getFeatureName() {
        return feature.getName();
    }

    public void setNextFeatureInstance(ConditionalExpression exp, NetworkFeatureInstance instance) {
        nextFeatureInstanceMap.put(exp, instance);
    }

    public NetworkFeatureInstance getNextInstance() {
        return nextFeatureInstanceMap.get(ConditionalExpression.TRUE);
    }

    public NetworkFeature getFeature() {
        return feature;
    }

    public NetworkFeatureInstance getNextInstance(ConditionalExpression expression) {
        return nextFeatureInstanceMap.get(expression);
    }
}

public class NetworkFeatureGraph {
    public static final NetworkFeatureGraph NULL_GRAPH = new NetworkFeatureGraph();

    private List<NetworkFeature> featureList;
    private List<NetworkFeatureInstance> instanceList;
    private Map<Integer, NetworkFeatureInstance> instanceMap;
    private NetworkFeatureInstance firstInstance;

    NetworkFeatureGraph() {
        instanceList = new ArrayList<>();
        instanceMap = new HashMap<>();
        featureList = new ArrayList<>();
        firstInstance = null;
    }


    public List<NetworkFeatureInstance> getInstanceList() {
        return instanceList;
    }

    public NetworkFeatureGraph addInstance(NetworkFeatureInstance instance) {
        if (firstInstance == null) {
            firstInstance = instance;
        }

        featureList.add(instance.getFeature());
        instanceList.add(instance);
        instanceMap.put(instance.getInstanceId(), instance);
        return this;
    }

    public NetworkFeatureGraph addInstance(NetworkFeature feature) throws LambdaCompilerException {
        NetworkFeatureInstance instance = new NetworkFeatureInstance(feature);
        addInstance(instance);
        return this;
    }


    public void insert(NetworkFeature feature, int index) {
        // TODO: Only satisfy service chaining
        this.featureList.add(index, feature);
    }

    public void insertBefore(NetworkFeature feature, NetworkFeature position) {
        // TODO: Only satisfy service chaining
        int index = this.featureList.indexOf(position);
        this.featureList.add(index, feature);
    }

    public void insertAfter(NetworkFeature feature, NetworkFeature position) {
        // TODO: Only satisfy service chaining
        int index = this.featureList.indexOf(position);
        this.featureList.add(index + 1, feature);
    }

    public List<NetworkFeature> getFeatureList() {
        return featureList;
    }


    public NetworkFeature getNetworkFeature(int index) {
        return featureList.get(index);
    }

    public NetworkFeatureInstance getFirstInstance() {
        return firstInstance;
    }

    public boolean containNetworkFeature(NetworkFeature feature) {
        return featureList.contains(feature);
    }

    public NetworkFeatureGraph copy() {
        NetworkFeatureGraph newGraph = new NetworkFeatureGraph();
        newGraph.firstInstance = this.firstInstance;
        newGraph.instanceList.addAll(this.instanceList);
        newGraph.instanceMap.putAll(this.instanceMap);
        newGraph.featureList.addAll(this.featureList);
        return newGraph;
    }

    public static NetworkFeatureGraph createGraph(NetworkFeature feature) {
        NetworkFeatureGraph graph = new NetworkFeatureGraph();
        try {
            graph.addInstance(feature);
        }
        catch (LambdaCompilerException e) {
            e.printStackTrace();
            return null;
        }
        return graph;
    }

    @Override
    public String toString() {
        String ret = "";
        for(NetworkFeatureInstance instance:instanceList) {
            if(!ret.equals("")) {
                ret += "->";
            }
            ret += instance.getFeatureName();
        }
        return ret;
    }
}
