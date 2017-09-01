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

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;
import org.netarch.util.OrderedHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Service
public class LambdaOrchestrator implements LambdaOrchestratorService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected LambdaCompilerService compileService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected LambdaVerifierService verifierService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected LambdaProviderService providerService;


    @Override
    public void install(String str) {
        LambdaPolicy policy = compileService.compile(str);

        if (policy == null) {
            log.info("Cannot compile the policy: " + str);
            return;
        }

        if(!verifierService.verify(policy)) {
            log.info("Policy" + str +" cannot pass the verification");
        }

        construct(policy);

    }

    @Override
    public void delete(String str) {

    }

    @Override
    public List<String> show() {
        return null;
    }

    @Override
    public void update(String str) {

    }

    private List<LambdaDevice> getDevicePath(LambdaPolicy policy) {
        LambdaDevice start = providerService.getStartDevice(policy);
        LambdaDevice end = providerService.getEndDevice(policy);

        Queue<LambdaDevice> deviceQueue = new LinkedList<>();
        Map<String, String> dpidMap = new HashMap<>();
        dpidMap.put(start.getDpid(), end.getDpid());
        deviceQueue.add(start);

        while(!deviceQueue.isEmpty()) {
            LambdaDevice device = deviceQueue.remove();
            for(LambdaDevice d:device.getPeerList()) {
                if(!dpidMap.containsKey(d.getDpid())) {
                    dpidMap.put(d.getDpid(), device.getDpid());
                    deviceQueue.add(d);
                    if (d == end) {
                        break;
                    }
                }
            }
        }

        if (dpidMap.get(end.getDpid()) == null) {
            return null;
        }


        List<LambdaDevice> devicePath = new ArrayList<>();
        List<String> dpidList = new ArrayList<>();

        String dpid = end.getDpid();
        while(!dpid.equals(start.getDpid())) {
            dpidList.add(dpid);
            dpid = dpidMap.get(dpid);
        }

        for(int i = dpidList.size() - 1; i >= 0; i--) {
            devicePath.add(providerService.getDevice(dpidList.get(i)));
        }

        return devicePath;
    }


    private NetworkFeatureGraph completeNetworkFeatureGraph(NetworkFeatureGraph graph, LambdaDevice device) {
        // TODO
        return null;
    }

    private Map<LambdaDevice, LambdaNode> createNodeMap(LambdaPolicy policy, List<LambdaDevice> devicePath) {
        OrderedHashMap<LambdaDevice, LambdaNode> nodeMap = new OrderedHashMap<>();

        for(LambdaDevice device:devicePath) {
            nodeMap.put(device, new LambdaNode(device.getDpid()));
        }
        int x = 0;
        for(LambdaNode node:policy.getPath().getNodeList()) {
            if(node.isNullGraph()) {
                // Do not process '.*'
                if(node.isRepeatable()) {
                    continue;
                }
                else {
                    // Use default network feature to process '.'
                    LambdaNode devNode = nodeMap.getValue(x);
                    LambdaDevice dev = nodeMap.getKey(x);

                    if(devNode.getGraph() == null) {
                        continue;
                    }

                    devNode.setGraph(NetworkFeatureGraph.createGraph(dev.getDefaultFeature()));
                    x = x + 1;
                }
            }
            else {
                if (node.isRepeatable()) {
                    // TODO:
                }
                else {
                    // (S1 NAT)
                    if (node.getDpid() != null) {
                        String dpid = node.getDpid();
                        LambdaDevice dev = providerService.getDevice(dpid);

                        if(dev == null) {
                            return null;
                        }

                        LambdaNode devNode = nodeMap.get(dev);

                        if (devNode.getGraph() != null) {
                            return null;
                        }

                        NetworkFeatureGraph graph = node.getGraph();

                        // Complete network feature graph.
                        NetworkFeatureGraph newGraph = completeNetworkFeatureGraph(graph, dev);
                        if (newGraph == null) {
                            return null;
                        }
                        devNode.setGraph(newGraph);

                    }
                    // (NAT)
                    else {
                        LambdaNode devNode = nodeMap.getValue(x);
                        LambdaDevice dev = nodeMap.getKey(x);


                        if (devNode.getGraph() == null) {
                            continue;
                        }

                        NetworkFeatureGraph graph = node.getGraph();

                        // Complete network feature graph.
                        NetworkFeatureGraph newGraph = completeNetworkFeatureGraph(graph, dev);
                        if (newGraph == null) {
                            return null;
                        }
                        devNode.setGraph(newGraph);
                        x = x + 1;
                    }
                }
            }
        }

        return nodeMap;
    }

    private List<LambdaProviderPolicy> generateProviderPolicies(Map<LambdaDevice, LambdaNode> nodeMap) {
        List<LambdaProviderPolicy> policyList = new ArrayList<>();

        // TODO

        return policyList;
    }

    private void construct(LambdaPolicy policy) {

        List<LambdaDevice> devicePath = getDevicePath(policy);

        if(devicePath == null) {
            return;
        }

        Map<LambdaDevice, LambdaNode> nodeMap = createNodeMap(policy, devicePath);

        if (nodeMap == null || nodeMap.size() == 0) {
            // TODO: Handle exception
            return;
        }

        List<LambdaProviderPolicy> policyList = generateProviderPolicies(nodeMap);

        if (policyList == null || policyList.size() == 0) {
            // TODO: Handle exception
            return;
        }

        providerService.installPolicies(policyList);

    }

}
