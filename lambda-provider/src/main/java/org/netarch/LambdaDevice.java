package org.netarch;

import org.onosproject.net.Device;

import java.util.*;

public class LambdaDevice {
    String dpid;
    Device device;
    NetworkFeature defaultFeature;
    List<NetworkFeature> featureList;
    List<LambdaDevice> peerList;

    public LambdaDevice(String dpid) {
        this.dpid = dpid;
        this.featureList =  new ArrayList<>();
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
        featureList.add(feature);
        return this;
    }

    public boolean hasFeature(String featureName) {
        if(!NetworkFeature.containFeatureInstance(featureName)) {
            return false;
        }
        return hasFeature(NetworkFeature.getFeatureInstance(featureName));
    }

    public boolean hasFeature(NetworkFeature feature) {
        return featureList.contains(feature);
    }

    public List<LambdaDevice> getPeerList() {
        return peerList;
    }

    public LambdaDevice addPeer(LambdaDevice device) {
        peerList.add(device);
        return this;
    }

    public List<NetworkFeature> getFeatureList() {
        return featureList;
    }

    public int getFeatureId(NetworkFeature feature) {
        return featureList.indexOf(feature);
    }

    @Override
    public int hashCode() {
        return this.dpid.hashCode();
    }

    @Override
    public String toString() {
        return "Device ("+dpid+")";
    }
}
