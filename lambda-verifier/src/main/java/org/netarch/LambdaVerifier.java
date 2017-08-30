package org.netarch;

import org.apache.felix.scr.annotations.Service;

@Service
public class LambdaVerifier implements LambdaVerifierService {
    @Override
    public boolean verify(LambdaPolicy policy) {
        return false;
    }
}
