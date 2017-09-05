package org.netarch;

import java.util.List;

public interface LambdaProviderService extends LambdaService{

    /**
     *
     * @param policy
     * @return
     */
    LambdaDevice getStartDevice(LambdaPolicy policy);

    /**
     *
     * @param policy
     * @return
     */
    LambdaDevice getEndDevice(LambdaPolicy policy);

    /**
     *
     * @param dpid
     * @return
     */
    LambdaDevice getDevice(String dpid);

    /**
     *
     * @param policy
     */
    void installPolicy(LambdaProviderPolicy policy);

    /**
     *
     * @param policyList
     */
    void installPolicies(List<LambdaProviderPolicy> policyList);

    /**
     *
     * @param policy
     */
    void updatePolicy(LambdaProviderPolicy policy);

    /**
     *
     * @param policyList
     */
    void updatePolicies(List<LambdaProviderPolicy> policyList);

    /**
     *
     * @param policy
     */
    void deletePolicy(LambdaProviderPolicy policy);

    /**
     *
     * @param policyList
     */
    void deletePolicies(List<LambdaProviderPolicy> policyList);

    /**
     *
     * @param networkEvent
     */
    void publishNetworkEvent(NetworkEvent networkEvent);

    /**
     *
     */
    void registerNetworkEventListener(NetworkEventListener listener);

    /**
     *
     */
    void removeNetworkEventListener(NetworkEventListener listener);
}
