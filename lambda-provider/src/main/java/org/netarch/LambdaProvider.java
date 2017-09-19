package org.netarch;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.google.common.collect.Lists;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;
import org.onosproject.bmv2.api.context.Bmv2Configuration;
import org.onosproject.bmv2.api.context.Bmv2DefaultConfiguration;
import org.onosproject.bmv2.api.context.Bmv2DeviceContext;
import org.onosproject.bmv2.api.runtime.Bmv2ExtensionSelector;
import org.onosproject.bmv2.api.runtime.Bmv2ExtensionTreatment;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.net.DeviceId;
import org.onosproject.net.device.DeviceEvent;
import org.onosproject.net.device.DeviceListener;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.flow.*;
import org.onosproject.net.flow.criteria.ExtensionSelector;
import org.onosproject.net.flow.instructions.ExtensionTreatment;
import org.onosproject.net.link.LinkEvent;
import org.onosproject.net.link.LinkListener;
import org.onosproject.net.link.LinkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.onlab.util.Tools.groupedThreads;

import static org.netarch.LambdaInterpreter.*;

@Service
public class LambdaProvider implements LambdaProviderService {

    private static final String APP_NAME = "org.onosproject.bmv2-lambda";
    private static final String JSON_CONFIG_PATH = "/lambda.json";

    private static final Bmv2Configuration LAMBDA_CONFIGURATION = loadConfiguration();
    private static final LambdaInterpreter LAMBDA_INTERPRETER = new LambdaInterpreter();
    protected static final Bmv2DeviceContext LAMBDA_CONTEXT = new Bmv2DeviceContext(LAMBDA_CONFIGURATION, LAMBDA_INTERPRETER);

    private ApplicationId appId;

    private static final int FLOW_PRIORITY = 100;
    private static short chainId = 0;

    private List<NetworkEventListener> networkEventListenerList;
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected DeviceService deviceService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected LinkService linkService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected CoreService coreService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected FlowRuleService flowRuleService;

    private LambdaLinkListener linkListener = new LambdaLinkListener();
    private LambdaDeviceListener deviceListener = new LambdaDeviceListener();

    private final ExecutorService executorService = Executors
            .newFixedThreadPool(8, groupedThreads("onos/lambda", "lambda-provider-task", log));

    public LambdaProvider() {
        networkEventListenerList = new ArrayList<>();
    }

    private void spawnTask(Runnable task) {
        executorService.execute(task);
    }

    @Override
    public LambdaDevice getStartDevice(LambdaPolicy policy) {
        return null;
    }

    @Override
    public LambdaDevice getEndDevice(LambdaPolicy policy) {
        return null;
    }

    @Override
    public LambdaDevice getDevice(String dpid) {
        return null;
    }

    @Override
    public void installPolicies(List<LambdaProviderPolicy> policyList) {
        for(LambdaProviderPolicy policy:policyList) {
            installPolicy(policy);
        }
    }

    @Override
    public void installPolicy(LambdaProviderPolicy policy) {
        doInstallPolicy(policy);
    }



    private void doInstallPolicy(LambdaProviderPolicy policy) {
        List<FlowRule> rules = generateRulesByPolicy(policy);
        flowRuleService.applyFlowRules((FlowRule[]) rules.toArray());
        policy.setInstalledFlowRules(rules);
    }


    @Override
    public void updatePolicies(List<LambdaProviderPolicy> policyList) {
        for(LambdaProviderPolicy policy:policyList) {
            updatePolicy(policy);
        }
    }

    @Override
    public void updatePolicy(LambdaProviderPolicy policy) {
        // check whether the policy is valid
        doUpdatePolicy(policy);
    }

    /**
     *
     */
    private void doUpdatePolicy(LambdaProviderPolicy policy) {
        doDeletePolicy(policy);
        doInstallPolicy(policy);
    }

    @Override
    public void deletePolicies(List<LambdaProviderPolicy> policyList) {
        for(LambdaProviderPolicy policy:policyList) {
            deletePolicy(policy);
        }
    }

    @Override
    public void deletePolicy(LambdaProviderPolicy policy) {
        // check whether policy is valid.
        doDeletePolicy(policy);
    }

    /**
     *
     */
    private void doDeletePolicy(LambdaProviderPolicy policy) {
        List<FlowRule> rules = policy.getInstalledFlowRules();
        flowRuleService.removeFlowRules((FlowRule[]) rules.toArray());
        policy.setInstalledFlowRules(new ArrayList<>());
    }


    @Override
    public void publishNetworkEvent(NetworkEvent networkEvent) {
        for(NetworkEventListener listener:networkEventListenerList) {
            if(listener.typeFilter(networkEvent.getType())) {
                listener.process(networkEvent);
            }
        }
    }

    @Override
    public void removeNetworkEventListener(NetworkEventListener listener) {
        this.networkEventListenerList.remove(listener);
    }

    @Override
    public void registerNetworkEventListener(NetworkEventListener listener) {
        this.networkEventListenerList.add(listener);
    }

    @Override
    public void activate() {
        deviceService.addListener(deviceListener);
        linkService.addListener(linkListener);
        appId = coreService.registerApplication(APP_NAME);
    }

    @Override
    public void deactivate() {
        try {
            executorService.shutdown();
            executorService.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            List<Runnable> runningTasks = executorService.shutdownNow();
            log.warn("Unable to stop the following tasks: {}", runningTasks);
        }
        deviceService.removeListener(deviceListener);
        linkService.removeListener(linkListener);

    }

    private class LambdaLinkListener implements LinkListener {
        @Override
        public void event(LinkEvent linkEvent) {
            if (linkEvent.type() == LinkEvent.Type.LINK_ADDED) {
                spawnTask(() -> {
                    publishNetworkEvent(NetworkEvent.createLinkUpEvent(linkEvent.subject()));
                });
            }
            else if (linkEvent.type() == LinkEvent.Type.LINK_REMOVED) {
                spawnTask(() -> {
                    publishNetworkEvent(NetworkEvent.createLinkDownEvent(linkEvent.subject()));
                });
            }
        }

        @Override
        public boolean isRelevant(LinkEvent event) {
            return true;
        }
    }

    private class LambdaDeviceListener implements DeviceListener {
        @Override
        public void event(DeviceEvent deviceEvent) {
            if (deviceEvent.type() == DeviceEvent.Type.DEVICE_ADDED) {
                spawnTask(() -> {
                    publishNetworkEvent(NetworkEvent.createNodeUpEvent(deviceEvent.subject()));
                });
            }
            else if (deviceEvent.type() == DeviceEvent.Type.DEVICE_REMOVED) {
                spawnTask(() -> {
                    publishNetworkEvent(NetworkEvent.createNodeUpEvent(deviceEvent.subject()));
                });
            }
        }

        @Override
        public boolean isRelevant(DeviceEvent event) {
            return true;
        }
    }


    private Bmv2ExtensionSelector buildPipelineStartSelector(int ipv4DstAddr, int ipv4SrcAddr, byte ipv4Proto,
                                                             short tcpDstPort, short tcpSrcPort) {
        return Bmv2ExtensionSelector.builder()
                .forConfiguration(LAMBDA_CONTEXT.configuration())
                .matchTernary("ipv4", "dst_addr", ipv4DstAddr, 0xFFFFFFFF)
                .matchTernary("ipv4", "src_addr", ipv4SrcAddr, 0xFFFFFFFF)
                .matchTernary("ipv4", "proto", ipv4Proto, 0xFF)
                .matchTernary("tcp", "dst_port", tcpDstPort, 0xFFFF)
                .matchTernary("tcp", "src_port", tcpSrcPort, 0xFFFF)
                .build();
    }

    private Bmv2ExtensionTreatment buildPipelineStartTreatment(short chainId, short bitMap) {
        return Bmv2ExtensionTreatment.builder()
                .forConfiguration(LAMBDA_CONTEXT.configuration())
                .setActionName("act_set_chain")
                .addParameter("chain_id", chainId)
                .addParameter("bitmap", bitMap)
                .build();
    }

    private Bmv2ExtensionSelector buildPipelineRewindSelector(short clickId, byte clickState) {
        return Bmv2ExtensionSelector.builder()
                .forConfiguration(LAMBDA_CONTEXT.configuration())
                .matchExact("click_metadata", "click_id", clickId)
                .matchExact("click_metadata", "click_state", clickState)
                .build();
    }

    private Bmv2ExtensionTreatment buildPipelineRewindTreatment(byte state, short bitMap) {
        return Bmv2ExtensionTreatment.builder()
                .forConfiguration(LAMBDA_CONTEXT.configuration())
                .setActionName("rewind")
                .addParameter("state", state)
                .addParameter("bitmap", bitMap)
                .build();
    }

    private List<FlowRule> generateRulesByPolicy(LambdaProviderPolicy policy) {
        DeviceId deviceId = policy.getDevice().device.id();
        FlowTuple flowTuple = policy.getLambdaId().flowTuple;
        List<Integer> bitMaps = policy.getBitmaps();
        Map<String, Integer> tableMap = LAMBDA_CONTEXT.interpreter().tableIdMap().inverse();

        ExtensionSelector pipeLineSelector;
        ExtensionTreatment pipeLineTreatment;
        FlowRule rule;
        List<FlowRule> rules = Lists.newArrayList();

        pipeLineSelector = buildPipelineStartSelector(
                flowTuple.getDstAddr().toInt(),
                flowTuple.getSrcAddr().toInt(),
                (byte) flowTuple.getProto(),
                (short) flowTuple.getDstPort().toInt(),
                (short) flowTuple.getDstPort().toInt()
        );

        chainId ++;

        pipeLineTreatment = buildPipelineStartTreatment(
                chainId,
                bitMaps.get(0).shortValue()
        );

        rule = DefaultFlowRule.builder()
                .forDevice(deviceId)
                .forTable(tableMap.get(TABLE_PIPELINE_START))
                .fromApp(appId)
                .makePermanent()
                .withPriority(FLOW_PRIORITY)
                .withSelector(
                        DefaultTrafficSelector.builder()
                                .extension(pipeLineSelector, deviceId)
                                .build())
                .withTreatment(
                        DefaultTrafficTreatment.builder()
                                .extension(pipeLineTreatment, deviceId)
                                .build())
                .build();

        // log.info("Installing policy rules to table {} of device {}", TABLE_PIPELINE_START, deviceId);
        rules.add(rule);

        for (int index = 1; index < bitMaps.size(); index++) {
            pipeLineSelector = buildPipelineRewindSelector(
                    chainId,
                    (byte)(index - 1)
            );
            pipeLineTreatment = buildPipelineRewindTreatment(
                    (byte)index,
                    bitMaps.get(index).shortValue()
            );
            rule = DefaultFlowRule.builder()
                    .forDevice(deviceId)
                    .forTable(tableMap.get(TABLE_PIPELINE_REWIND))
                    .fromApp(appId)
                    .makePermanent()
                    .withPriority(FLOW_PRIORITY)
                    .withSelector(
                            DefaultTrafficSelector.builder()
                                    .extension(pipeLineSelector, deviceId)
                                    .build())
                    .withTreatment(
                            DefaultTrafficTreatment.builder()
                                    .extension(pipeLineTreatment, deviceId)
                                    .build())
                    .build();
            log.info("Installing policy rules to table {} of device {}", TABLE_PIPELINE_REWIND, deviceId);
            rules.add(rule);
        }

        return rules;
    }

    private static Bmv2Configuration loadConfiguration() {
        try {
            JsonObject json = Json.parse(new BufferedReader(new InputStreamReader(
                    LambdaProvider.class.getResourceAsStream(JSON_CONFIG_PATH)))).asObject();
            return Bmv2DefaultConfiguration.parse(json);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load lambda json configuration", e);
        }
    }
}
