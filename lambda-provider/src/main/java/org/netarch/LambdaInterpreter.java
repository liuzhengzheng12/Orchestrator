package org.netarch;

import com.google.common.collect.ImmutableBiMap;
import org.onosproject.bmv2.api.context.Bmv2Configuration;
import org.onosproject.bmv2.api.context.Bmv2Interpreter;
import org.onosproject.bmv2.api.context.Bmv2InterpreterException;
import org.onosproject.bmv2.api.runtime.Bmv2Action;
import org.onosproject.net.flow.TrafficTreatment;
import org.onosproject.net.flow.criteria.Criterion;

public class LambdaInterpreter implements Bmv2Interpreter {

    protected static final String TABLE_PIPELINE_START = "tbl_pipeline_start";
    protected static final String TABLE_PIPELINE_REWIND = "rewind_table";

    private static final ImmutableBiMap<Integer, String> TABLE_ID_MAP = ImmutableBiMap.of(
            0, TABLE_PIPELINE_START,
            1, TABLE_PIPELINE_REWIND
    );

    @Override
    public ImmutableBiMap<Integer, String> tableIdMap() {
        return TABLE_ID_MAP;
    }

    @Override
    public ImmutableBiMap<Criterion.Type, String> criterionTypeMap() {
        return null;
    }

    @Override
    public Bmv2Action mapTreatment(TrafficTreatment var1, Bmv2Configuration var2)
            throws Bmv2InterpreterException {
        return null;
    }
}
