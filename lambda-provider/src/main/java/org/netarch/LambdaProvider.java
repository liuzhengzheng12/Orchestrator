package org.netarch;

import org.apache.felix.scr.annotations.Service;

import java.util.List;

@Service
public class LambdaProvider implements LambdaProviderService {
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

    private void doUpdatePolicy() {

    }
}
