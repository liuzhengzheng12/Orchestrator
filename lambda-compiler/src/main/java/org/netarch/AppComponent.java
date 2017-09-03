/*
 * Copyright 2017-present Network Architecture Laboratory, Tsinghua University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.netarch;

import org.apache.felix.scr.annotations.*;
import org.apache.felix.scr.annotations.Service;
import org.netarch.utils.IndentPrintWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * Skeletal ONOS application component.
 */
@Component(immediate = true)
public class AppComponent {

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected LambdaCompilerService compilerService = new LambdaCompiler();

    private final Logger log = LoggerFactory.getLogger(getClass());

    private String dstFileName = "output.txt";

    @Activate
    protected void activate() {
        log.info("Started");

        String testStr = "(ipv4.src = 192.168.0.2 and ipv4.dst = 192.168.1.2 and tcp.dst = 80) -> (ip_sourceguard) .* (s1 ipv4 fib ipv4 urpf nexthop mac rewrite nat) .∗";
        if (compilerService != null) {
            log.info("Register Lambda compiler service.");
            LambdaPolicy policy = compilerService.compile(testStr);
            assert (policy != null);

            try {
                IndentPrintWriter pw = new IndentPrintWriter(new PrintStream(new FileOutputStream(dstFileName)), 4);
                policy.printTo(pw);
            } catch (FileNotFoundException e) {
                System.err.print("File not found");
                System.exit(1);
            }

        }
        else {
            log.error("Cannot register Lambda orchestrator service.");
        }
    }

    @Deactivate
    protected void deactivate() {
        log.info("Stopped");
    }

}
