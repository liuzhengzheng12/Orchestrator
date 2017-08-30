package org.netarch;

import com.google.common.collect.ImmutableBiMap;
import org.onosproject.bmv2.api.context.Bmv2Configuration;
import org.onosproject.bmv2.api.context.Bmv2Interpreter;
import org.onosproject.bmv2.api.context.Bmv2InterpreterException;
import org.onosproject.bmv2.api.runtime.Bmv2Action;
import org.onosproject.net.flow.TrafficTreatment;
import org.onosproject.net.flow.criteria.Criterion;

public class LambdaInterpreter implements Bmv2Interpreter {

    @Override
    public ImmutableBiMap<Criterion.Type, String> criterionTypeMap() {
        return null;
    }

    @Override
    public Bmv2Action mapTreatment(TrafficTreatment trafficTreatment, Bmv2Configuration bmv2Configuration) throws Bmv2InterpreterException {
        return null;
    }

    @Override
    public ImmutableBiMap<Integer, String> tableIdMap() {
        return null;
    }
}
