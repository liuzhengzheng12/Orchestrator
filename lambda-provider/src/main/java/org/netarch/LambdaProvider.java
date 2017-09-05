package org.netarch;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;
import org.onosproject.net.Link;
import org.onosproject.net.device.DeviceEvent;
import org.onosproject.net.device.DeviceListener;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.link.LinkEvent;
import org.onosproject.net.link.LinkListener;
import org.onosproject.net.link.LinkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.onlab.util.Tools.groupedThreads;

@Service
public class LambdaProvider implements LambdaProviderService {

    private List<NetworkEventListener> networkEventListenerList;
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected DeviceService deviceService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected LinkService linkService;

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

    }

    /**
     *
     */
    private void doInstallPolicy() {

    }

    @Override
    public void updatePolicies(List<LambdaProviderPolicy> policyList) {
        for(LambdaProviderPolicy policy:policyList) {
            updatePolicy(policy);
        }
    }

    @Override
    public void updatePolicy(LambdaProviderPolicy policy) {

    }

    /**
     *
     */
    private void doUpdatePolicy() {

    }

    @Override
    public void deletePolicies(List<LambdaProviderPolicy> policyList) {
        for(LambdaProviderPolicy policy:policyList) {
            deletePolicy(policy);
        }
    }

    @Override
    public void deletePolicy(LambdaProviderPolicy policy) {

    }

    /**
     *
     */
    private void doDeletePolicy() {

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

}
