/*
 * Copyright 2017-present Open Networking Foundation
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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

/**
 * Set of tests of the ONOS application component.
 */
public class AppComponentTest {

    private LambdaCompiler compiler;
    private String testPolicies = "policies.txt";
    private String testFeatures = "features.txt";


    @Before
    public void setUp() {


    }

    @After
    public void tearDown() {

    }

    @Test
    public void basics() {

        try {
            InputStream stream = getClass().getClassLoader().getResourceAsStream(testFeatures);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while((line = reader.readLine()) != null) {
                if(line.endsWith("\n")) {
                    line = line.substring(line.length() - 1);
                }
                NetworkFeature.registerFeature(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        compiler = new LambdaCompiler();

        try {
            InputStream stream = AppComponentTest.class.getClassLoader().getResourceAsStream(testPolicies);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while((line = reader.readLine()) != null) {
                LambdaPolicy policy = compiler.compile(line);
                System.out.println(policy);
            }
        }
        catch (LambdaCompilerException e) {
            System.out.println(e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
