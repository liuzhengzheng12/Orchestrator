package org.netarch;

import java.util.ArrayList;
import java.util.List;

/**
 * Orchestration policy.
 */
public class LambdaProviderPolicy {
    LambdaDevice device;
    LambdaFlowIdentifier lambdaId;
    List<Integer> bitmaps;


    public LambdaProviderPolicy() {
        this.device = null;
        this.lambdaId = null;
        this.bitmaps = new ArrayList<>();
    }

    public LambdaProviderPolicy addBitmap(int bitmap) {
        this.bitmaps.add(new Integer(bitmap));
        return this;
    }

    public List<Integer> getBitmaps() {
        return bitmaps;
    }

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
