package org.netarch;

import java.util.List;

public interface LambdaProviderService {
    LambdaDevice getStartDevice(LambdaPolicy policy);
    LambdaDevice getEndDevice(LambdaPolicy policy);
    LambdaDevice getDevice(String dpid);
    void installPolicy(LambdaProviderPolicy policy);
    void installPolicies(List<LambdaProviderPolicy> policyList);
    void updatePolicy(LambdaProviderPolicy policy);
    void updatePolicies(List<LambdaProviderPolicy> policyList);
}
