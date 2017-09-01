package org.netarch;

import org.onosproject.net.Device;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LambdaDevice {
    String dpid;
    Device device;
    NetworkFeature defaultFeature;
    Set<NetworkFeature> featureSet;
    List<LambdaDevice> peerList;

    public LambdaDevice(String dpid) {
        this.dpid = dpid;
        this.featureSet =  new HashSet<>();
        this.defaultFeature = null;
        this.device = null;
    }

    public LambdaDevice setDefaultFeature(NetworkFeature defaultFeature) {
        this.defaultFeature = defaultFeature;
        return this;
    }

    public NetworkFeature getDefaultFeature() {
        return defaultFeature;
    }

    public Device getDevice() {
        return device;
    }

    public LambdaDevice setDevice(Device device) {
        this.device = device;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LambdaDevice)) {
            return false;
        }
        LambdaDevice device = (LambdaDevice) obj;
        return this.dpid.equals(device.dpid);
    }

    public String getDpid() {
        return dpid;
    }

    public LambdaDevice addFeature(NetworkFeature feature) {
        featureSet.add(feature);
        return this;
    }

    public boolean hasFeature(String featureName) {
        if(!NetworkFeature.containFeatureInstance(featureName)) {
            return false;
        }
        return hasFeature(NetworkFeature.getFeatureInstance(featureName));
    }

    public boolean hasFeature(NetworkFeature feature) {
        return featureSet.contains(feature);
    }

    public List<LambdaDevice> getPeerList() {
        return peerList;
    }

    public LambdaDevice addPeer(LambdaDevice device) {
        peerList.add(device);
        return this;
    }

    @Override
    public int hashCode() {
        return this.dpid.hashCode();
    }
}
