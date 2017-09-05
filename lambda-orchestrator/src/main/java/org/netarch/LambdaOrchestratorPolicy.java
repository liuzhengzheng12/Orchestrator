package org.netarch;

import java.util.Map;

public class LambdaOrchestratorPolicy {
    private Map<LambdaDevice, LambdaNode> nodeMap;
    private LambdaFlowIdentifier flowIdentifier;


    LambdaOrchestratorPolicy(LambdaFlowIdentifier flowIdentifier, Map<LambdaDevice,
            LambdaNode> nodeMap) {
        this.nodeMap = nodeMap;
        this.flowIdentifier = flowIdentifier;
    }

    @Override
    public int hashCode() {
        return flowIdentifier.hashCode();
    }

    public LambdaFlowIdentifier getFlowIdentifier() {
        return flowIdentifier;
    }

    public Map<LambdaDevice, LambdaNode> getNodeMap() {
        return nodeMap;
    }

    /**
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }

        if(!(obj instanceof LambdaOrchestratorPolicy)) {
            return false;
        }

        LambdaOrchestratorPolicy policy = (LambdaOrchestratorPolicy) obj;
        return policy.flowIdentifier.equals(this.flowIdentifier);
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        String ret = "";
        for(LambdaDevice device:nodeMap.keySet()) {
            if (!ret.equals("")) {
                ret += "";
            }
            ret += "(" + device.toString() + " "+ nodeMap.get(device).toString() + ")";
        }
        return flowIdentifier.toString() + " : " + ret;
    }
}
