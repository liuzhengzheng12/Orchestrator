package org.netarch;

import org.onlab.packet.Ip4Address;
import org.onlab.packet.TpPort;

import java.util.HashMap;
import java.util.Map;

class FlowTuple {
    private Ip4Address srcAddr;
    private Ip4Address dstAddr;
    private int proto;
    private TpPort srcPort;
    private TpPort dstPort;

    public FlowTuple(Ip4Address srcAddr, Ip4Address dstAddr, int proto, TpPort srcPort, TpPort dstPort) {
        this.srcAddr = srcAddr;
        this.dstAddr = dstAddr;
        this.proto = proto;
        this.dstPort = dstPort;
        this.srcPort = srcPort;
    }

    public int getProto() {
        return proto;
    }

    public Ip4Address getDstAddr() {
        return dstAddr;
    }

    public Ip4Address getSrcAddr() {
        return srcAddr;
    }

    public TpPort getDstPort() {
        return dstPort;
    }

    public TpPort getSrcPort() {
        return srcPort;
    }

    @Override
    public int hashCode() {
        int ret = 1;
        ret += srcAddr.hashCode();
        ret = ret * 63;
        ret += dstAddr.hashCode();
        ret = ret * 63;
        ret += proto;
        ret = ret * 63;
        ret += srcPort.hashCode();
        ret = ret * 63;
        ret += dstPort.hashCode();
        return ret;
    }
}

public class LambdaFlowIdentifier {
    private static int FLOW_ID_COUNTER = 0;
    private static Map<FlowTuple, LambdaFlowIdentifier> flowMap;

    static {
        flowMap = new HashMap<>();
    }


    int flowId;
    FlowTuple flowTuple;

    private LambdaFlowIdentifier(FlowTuple flowTuple) {
        FLOW_ID_COUNTER += 1;
        this.flowId = FLOW_ID_COUNTER;
        this.flowTuple = flowTuple;

    }

    public int getFlowId() {
        return flowId;
    }


    public static LambdaFlowIdentifier createIdentifier(FlowTuple tuple) {
        if (tuple == null) {
            return null;
        }

        if (flowMap.containsKey(tuple)) {
            return  null;
        }

        LambdaFlowIdentifier flowIdentifier = new LambdaFlowIdentifier(tuple);

        flowMap.put(tuple, flowIdentifier);

        return  flowIdentifier;
    }

    public static LambdaFlowIdentifier deleteIdentifier(FlowTuple tuple) {
        if (!flowMap.containsKey(tuple)) {
            return null;
        }
        return flowMap.remove(tuple);
    }


    @Override
    public String toString() {
        String ret = "[";
        ret += "FLOW_ID:" + flowId;
        ret += " ,IP.DST:" +  flowTuple.getDstAddr();
        ret += " ,IP.SRC:" + flowTuple.getSrcAddr();
        ret += " ,IP.PROTO:" + flowTuple.getProto();
        ret += " ,TP.DST:" + flowTuple.getDstPort();
        ret += " ,TP.SRC:" + flowTuple.getSrcPort();
        ret += "]";
        return ret;
    }
}
