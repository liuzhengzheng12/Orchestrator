package org.netarch;

import org.apache.felix.scr.annotations.Service;

@Service
public class LambdaChecker implements LambdaCheckerService {
    @Override
    public boolean verify(LambdaPolicy policy) {
        return false;
    }
}
