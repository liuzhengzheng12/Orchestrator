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

import java.util.HashMap;
import java.util.Map;

public class NetworkFeature {
    private static Map<String, NetworkFeature> FEATURE_MAP;
    static {
        FEATURE_MAP = new HashMap<>();
    }

    private String name;

    private NetworkFeature(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public static NetworkFeature getFeatureInstance(String name) {
        return FEATURE_MAP.get(name);
    }

    public static boolean containFeatureInstance(String name) {
        return FEATURE_MAP.containsKey(name);
    }

    public static void registerFeature(String name) {
        if (name != null) {
            if (!containFeatureInstance(name)) {
                FEATURE_MAP.put(name, new NetworkFeature(name));
            }
        }
    }

}
