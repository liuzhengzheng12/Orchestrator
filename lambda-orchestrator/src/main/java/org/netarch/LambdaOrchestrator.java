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
import org.onosproject.store.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Service
public class LambdaOrchestrator implements LambdaOrchestratorService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected LambdaCompilerService compileService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected LambdaCheckerService checkerService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected LambdaProviderService providerService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected StorageService storageService;


    private NetworkEventListener maintainer = new LambdaMaintainer();


    private LambdaOrchestratorPolicyStore policyStore;


    public LambdaOrchestrator() {
        this.policyStore = new LambdaOrchestratorPolicyStore(storageService);
        LambdaMaintainer maintainer = new LambdaMaintainer();
        providerService.registerNetworkEventListener(maintainer);
    }


    /**
     *
     * @return
     */
    public LambdaOrchestratorPolicyStore getPolicyStore() {
        return policyStore;
    }

    /**
     *
     * @param str
     */
    @Override
    public void install(String str) {
        LambdaPolicy policy = null;
        try {
            policy = compileService.compile(str);
        } catch (LambdaCompilerException e) {
            e.printStackTrace();
        }

        if (policy == null) {
            log.info("Cannot compile the policy: " + str);
            return;
        }

        if(!checkerService.verify(policy)) {
            log.info("Policy" + str +" cannot pass the verification");
        }

        try {
            doInstall(policy);
        }
        catch (LambdaOrchestratorException e) {
            e.printMessage();
        }
    }

    /**
     *
     * @param str
     */
    @Override
    public void delete(String str) {
        LambdaPolicy policy = null;
        try {
            policy = compileService.compile(str);
        } catch (LambdaCompilerException e) {
            e.printStackTrace();
        }

        try {
            doDelete(policy);
        }
        catch (LambdaOrchestratorException e) {
            e.printMessage();
        }
    }


    /**
     *
     * @return
     */
    @Override
    public List<String> show() {
        List<String> policyList = new ArrayList<>();
        for(LambdaOrchestratorPolicy policy:policyStore.getPolicyStore()) {
            policyList.add(policy.toString());
        }
        return policyList;
    }

    /**
     *
     * @param str
     */
    @Override
    public void update(String str) {
        LambdaPolicy policy = null;
        try {
            policy = compileService.compile(str);
        } catch (LambdaCompilerException e) {
            e.printStackTrace();
        }

        if (policy == null) {
            log.info("Cannot compile the policy: " + str);
            return;
        }

        if(!checkerService.verify(policy)) {
            log.info("Policy" + str +" cannot pass the verification");
        }

        try {
            doUpdate(policy);
        }
        catch (LambdaOrchestratorException e) {
            e.printMessage();
        }
    }

    /**
     *
     * @param policy
     * @return
     */
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

    /**
     *
     * @param graph
     * @param device
     * @return
     */
    private NetworkFeatureGraph completeNetworkFeatureGraph(NetworkFeatureGraph graph,
                                                            LambdaDevice device) {
        NetworkFeatureGraph newGraph = graph.copy();

        // TODO: Only satisfy service chaining

        for(int i = 0; i < newGraph.getFeatureList().size(); i++) {
            NetworkFeature nf = newGraph.getNetworkFeature(i);
            for(NetworkFeature t:nf.getPostDependencies()) {
                if(!newGraph.containNetworkFeature(t)) {
                    newGraph.insertAfter(nf, t);
                }
            }
            for(NetworkFeature t:nf.getPreDependencies()) {
                if(!newGraph.containNetworkFeature(t)) {
                    newGraph.insertBefore(nf, t);
                }
            }
        }
        return newGraph;
    }

    /**
     *
     * @param policy
     * @param devicePath
     * @return
     */
    private Map<LambdaDevice, LambdaNode> createNodeMap(LambdaPolicy policy,
                                                        List<LambdaDevice> devicePath) {
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

    /**
     *
     * @param nodeMap
     * @return
     */
    private List<LambdaProviderPolicy> generateProviderPolicies(Map<LambdaDevice,
            LambdaNode> nodeMap) {
        List<LambdaProviderPolicy> policyList = new ArrayList<>();

        for(LambdaDevice dev:nodeMap.keySet()) {
            LambdaNode node = nodeMap.get(dev);
            LambdaProviderPolicy providerPolicy = new LambdaProviderPolicy();
            providerPolicy.setDevice(dev);

            // The node should have completed network feature graph.
            if(node.isNullGraph() || node.isRepeatable()) {
                return null;
            }

            int bitmap = 0;
            int pre = 0;
            for(NetworkFeatureInstance instance:node.getGraph().getInstanceList()) {
                int id = dev.getFeatureId(instance.getFeature());

                // If the device do not support the feature.
                if (id < -1) {
                    return null;
                }

                if (id < pre) {
                    // A provider policy can have several bitmaps.
                    providerPolicy.addBitmap(bitmap);
                    pre = 0;
                    bitmap = 0;
                }
                else {
                    pre = id;
                    bitmap |= 1 << id;
                }
            }

            // Add the policy into the list.
            policyList.add(providerPolicy);
        }
        return policyList;
    }

    private static final String IP_SRC = "ip.src_addr";
    private static final String IP_DST = "ip.src_addr";
    private static final String IP_PROTO = "ipv4.proto";
    private static final String TP_SRC = "tp.src_addr";
    private static final String TP_DST = "tp.src_addr";

    /**
     *
     * @param predicate
     * @return
     */
    private FlowTuple createFlowTuple(LambdaPredicate predicate) {
        return new FlowTuple(predicate.getAtomicPredicate(IP_SRC).getValue().getIp4Address(),
                predicate.getAtomicPredicate(IP_DST).getValue().getIp4Address(),
                predicate.getAtomicPredicate(IP_PROTO).getValue().getInt32(),
                predicate.getAtomicPredicate(TP_SRC).getValue().getTpPort(),
                predicate.getAtomicPredicate(TP_DST).getValue().getTpPort());
    }

    /**
     *
     * @param policy
     * @param nodeMap
     * @return
     */
    private LambdaOrchestratorPolicy createOrchestratorPolicy(LambdaPolicy policy,
                                                              Map<LambdaDevice, LambdaNode> nodeMap) {
        LambdaPredicate predicate = policy.getPredicate();
        LambdaFlowIdentifier identifier = LambdaFlowIdentifier.createIdentifier(createFlowTuple(predicate));
        return new LambdaOrchestratorPolicy(identifier,
                nodeMap);
    }

    /**
     * Execute the addition of Lambda policies.
     * @param policy
     * @throws LambdaOrchestratorException
     */
    private void doInstall(LambdaPolicy policy) throws LambdaOrchestratorException {

        List<LambdaDevice> devicePath = getDevicePath(policy);

        if(devicePath == null) {
            return;
        }

        Map<LambdaDevice, LambdaNode> nodeMap = createNodeMap(policy, devicePath);

        if (nodeMap == null || nodeMap.size() == 0) {
            throw new LambdaOrchestratorException("The node map is null or empty.");
        }

        LambdaOrchestratorPolicy orchestratorPolicy = createOrchestratorPolicy(policy, nodeMap);

        if(!policyStore.addPolicy(orchestratorPolicy)) {
            // If the policy store has the policy, then we should install this policy.
            // Overwriting policies is not allowed in the install primitive.
            return;
        }

        List<LambdaProviderPolicy> policyList = generateProviderPolicies(nodeMap);


        if (policyList == null || policyList.size() == 0) {
            throw new LambdaOrchestratorException("The policy list is null or empty.");
        }

        providerService.installPolicies(policyList);

    }

    /**
     * Execute the deletion of Lambda policies.
     * @param policy
     * @throws LambdaOrchestratorException
     */
    private void doDelete(LambdaPolicy policy) throws LambdaOrchestratorException {

        LambdaFlowIdentifier identifier =
                LambdaFlowIdentifier.createIdentifier(createFlowTuple(policy.getPredicate()));

        LambdaOrchestratorPolicy orchestratorPolicy = policyStore.deletePolicy(identifier);

        if (orchestratorPolicy == null) {
            return;
        }

        List<LambdaProviderPolicy> policyList =
                generateProviderPolicies(orchestratorPolicy.getNodeMap());

        providerService.deletePolicies(policyList);

    }


    /**
     * Execute the update of Lambda policies.
     * @param policy
     * @throws LambdaOrchestratorException
     */
    private void doUpdate(LambdaPolicy policy) throws LambdaOrchestratorException {
        List<LambdaDevice> devicePath = getDevicePath(policy);

        if(devicePath == null) {
            return;
        }

        Map<LambdaDevice, LambdaNode> nodeMap = createNodeMap(policy, devicePath);

        if (nodeMap == null || nodeMap.size() == 0) {
            throw new LambdaOrchestratorException("The node map is null or empty.");
        }

        LambdaOrchestratorPolicy orchestratorPolicy = createOrchestratorPolicy(policy, nodeMap);

        if(!policyStore.updatePolicy(orchestratorPolicy)) {
            // If the policy store has the policy, then we should install this policy.
            // Overwriting policies is not allowed in the install primitive.
            return;
        }

        List<LambdaProviderPolicy> policyList = generateProviderPolicies(nodeMap);


        if (policyList == null || policyList.size() == 0) {
            throw new LambdaOrchestratorException("The policy list is null or empty.");
        }

        providerService.updatePolicies(policyList);
    }

    private class LambdaMaintainer implements NetworkEventListener {
        @Override
        public void process(NetworkEvent event) {

        }

        @Override
        public boolean typeFilter(NetworkEvent.NetworkEventType type) {
            return true;
        }
    }

    @Override
    public void activate() {
        providerService.registerNetworkEventListener(maintainer);
    }

    @Override
    public void deactivate() {
        providerService.removeNetworkEventListener(maintainer);
    }
}
