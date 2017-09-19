package org.netarch;

import org.onosproject.net.flow.FlowRule;

import java.util.ArrayList;
import java.util.List;

/**
 * Orchestration policy.
 */
public class LambdaProviderPolicy {
    LambdaDevice device;
    LambdaFlowIdentifier lambdaId;
    List<Integer> bitmaps;
    List<FlowRule> installedFlowRules;

    public LambdaProviderPolicy() {
        this.device = null;
        this.lambdaId = null;
        this.bitmaps = new ArrayList<>();
        this.installedFlowRules = new ArrayList<>();
    }

    public LambdaProviderPolicy addBitmap(int bitmap) {
        this.bitmaps.add(new Integer(bitmap));
        return this;
    }

    public List<Integer> getBitmaps() {
        return bitmaps;
    }

    public LambdaProviderPolicy setInstalledFlowRules(List<FlowRule> rules) {
        this.installedFlowRules = rules;
        return this;
    }

    public List<FlowRule> getInstalledFlowRules() { return installedFlowRules; }

    public LambdaProviderPolicy setLambdaId(LambdaFlowIdentifier lambdaId) {
        this.lambdaId = lambdaId;
        return this;
    }

    public LambdaFlowIdentifier getLambdaId() {
        return lambdaId;
    }

    public LambdaProviderPolicy setDevice(LambdaDevice device) {
        this.device = device;
        return this;
    }

    public LambdaDevice getDevice() {
        return device;
    }


}
