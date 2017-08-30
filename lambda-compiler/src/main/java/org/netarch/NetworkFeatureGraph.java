package org.netarch;


import javax.print.attribute.standard.NumberUp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ConditionalExpression {
    public static ConditionalExpression TRUE = new ConditionalExpression();
    private String exp;
    private ConditionalExpression() {
        exp = null;
    }

    public ConditionalExpression(String exp) throws CompilerException {
        this.exp = exp;
        if (this.exp == null) {
            throw new CompilerException("Conditional expresson can not be null");
        }
    }
}

class NetworkFeatureInstance {
    private static int INSTANCE_COUNTER = 0;
    public final static NetworkFeatureInstance NULL_INSTANCE = new NetworkFeatureInstance();

    private NetworkFeature feature;
    private int instanceId;
    private Map<ConditionalExpression, NetworkFeatureInstance> nextFeatureInstanceMap;


    private NetworkFeatureInstance() {
        this.feature = null;
        this.instanceId = INSTANCE_COUNTER;
        INSTANCE_COUNTER += 1;
    }

    NetworkFeatureInstance(NetworkFeature feature) throws CompilerException {
        this.instanceId = INSTANCE_COUNTER;
        this.feature = feature;
        if (this.feature == null) {
            throw new CompilerException("Cannot create an instance with a null network feature.");
        }

        this.nextFeatureInstanceMap = new HashMap<>();

        INSTANCE_COUNTER += 1;
    }

    public int getInstanceId() {
        return this.instanceId;
    }

    @Override
    public int hashCode() {
        return instanceId;
    }

    public String getFeatureName() {
        return feature.getName();
    }

    public void setNextFeatureInstance(ConditionalExpression exp, NetworkFeatureInstance instance) {
        nextFeatureInstanceMap.put(exp, instance);
    }

    public NetworkFeatureInstance getNextInstance() {
        return nextFeatureInstanceMap.get(ConditionalExpression.TRUE);
    }

    public NetworkFeature getFeature() {
        return feature;
    }

    public NetworkFeatureInstance getNextInstance(ConditionalExpression expression) {
        return nextFeatureInstanceMap.get(expression);
    }
}

public class NetworkFeatureGraph {
    private List<NetworkFeatureInstance> instanceList;
    private Map<Integer, NetworkFeatureInstance> instanceMap;
    private NetworkFeatureInstance firstInstance;

    NetworkFeatureGraph() {
        instanceList = new ArrayList<>();
        instanceMap = new HashMap<>();
        firstInstance = null;
    }

    public void addInstance(NetworkFeatureInstance instance) {
        if (firstInstance == null) {
            firstInstance = instance;
        }

        instanceList.add(instance);
        instanceMap.put(instance.getInstanceId(), instance);
    }
}
