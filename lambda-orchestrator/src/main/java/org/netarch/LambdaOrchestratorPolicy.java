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
}
