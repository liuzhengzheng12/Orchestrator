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

import org.onlab.packet.Ip4Address;
import org.onlab.packet.IpAddress;
import org.onlab.packet.TpPort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FieldValue {
    private List<Byte> bytes;
    private static Map<String, Byte> NUMBER_MAP;

    /**
     * Aid parsing
     */
    static {
        NUMBER_MAP = new HashMap<>();
        NUMBER_MAP.put("0", (byte) 0);
        NUMBER_MAP.put("1", (byte) 1);
        NUMBER_MAP.put("2", (byte) 2);
        NUMBER_MAP.put("3", (byte) 3);
        NUMBER_MAP.put("4", (byte) 4);
        NUMBER_MAP.put("5", (byte) 5);
        NUMBER_MAP.put("6", (byte) 6);
        NUMBER_MAP.put("7", (byte) 7);
        NUMBER_MAP.put("8", (byte) 8);
        NUMBER_MAP.put("9", (byte) 9);
        NUMBER_MAP.put("A", (byte) 10);
        NUMBER_MAP.put("B", (byte) 11);
        NUMBER_MAP.put("C", (byte) 12);
        NUMBER_MAP.put("D", (byte) 13);
        NUMBER_MAP.put("E", (byte) 14);
        NUMBER_MAP.put("F", (byte) 15);
        NUMBER_MAP.put("a", (byte) 10);
        NUMBER_MAP.put("b", (byte) 11);
        NUMBER_MAP.put("c", (byte) 12);
        NUMBER_MAP.put("d", (byte) 13);
        NUMBER_MAP.put("e", (byte) 14);
        NUMBER_MAP.put("f", (byte) 15);

    }

    /**
     * Automatically Parse Value
     * @param str value string
     */
    public FieldValue(String str) {
        bytes = new ArrayList<>();
        if (str.startsWith("0x")) {
            int i = 2;
            if (str.length() % 2 == 1) {
                bytes.add(NUMBER_MAP.get(str.substring(i, i + 1)));
                i = i + 1;
            }
            for (; i < str.length(); i += 2) {
                bytes.add((byte) ((NUMBER_MAP.get(str.substring(i, i + 1)) << 4) +
                        NUMBER_MAP.get(str.substring(i + 1, i + 2))));
            }
        }
        else if (str.startsWith("0b") | str.startsWith("0B")) {
            int i = 0;
            if (((str.length() - 2) % 8) > 0) {
                byte b = 0;
                for (i = 0; i < ((str.length() - 2) % 8); i++) {
                    b = (byte) (b << 1);
                    b += NUMBER_MAP.get(str.substring(i + 2, i + 3));
                }
                bytes.add(b);
            }


            i += 2;
            for (;i < str.length() - 2; i+=8) {
                byte b = 0;
                for (int j = 0; j < 8; j++) {
                    b = (byte) (b << 1);
                    b += NUMBER_MAP.get(str.substring(i + j, i + j + 1));
                }
                bytes.add(b);
            }

        }
        else if (str.contains(".")) {
            for(String b : str.split("\\.")) {
                int i = Integer.parseInt(b);
                bytes.add((byte) i);
            }


        }
        else if (str.contains(":")) {
            for(String b : str.split(":")) {
                int t = Integer.parseInt(b);
                bytes.add((byte) (t / 256));
                bytes.add((byte) (t % 256));
            }
        }
        else {
            long b = Long.parseLong(str);
            while(b > 0) {
                long t = b;
                while(t > 256) {
                    t >>= 8;
                }
                bytes.add((byte) t);
                b = b / 256;
            }
        }
    }

    /**
     *
     * @return int number
     */
    public int getInt32() {
        int tmp = 0;
        for(byte b : bytes) {
            tmp = tmp | b;
            tmp = tmp << 8;
        }
        return tmp;
    }

    /**
     *
     * @return Long number
     */
    public long getInt64() {
        long tmp = 0;
        for(byte b : bytes) {
            tmp = tmp | b;
            tmp = tmp << 8;
        }
        return tmp;
    }

    /**
     *
     * @return bytes
     */
    public List getBytes() {
        return bytes;
    }

    /**
     *
     * @return width in terms of bits
     */
    public int getBitWidth() {
        return 8*bytes.size();
    }

    /**
     *
     * @return
     */
    public byte[] getArray() {
        byte[] data = new byte[bytes.size()];
        return data;
    }

    /**
     *
     * @return IPv4 address (32 bits)
     */
    public Ip4Address getIp4Address() {
        return Ip4Address.valueOf(getArray());
    }

    /**
     *
     * @return Transport layer port (16 bits)
     */
    public TpPort getTpPort() {
        return TpPort.tpPort(getInt32());
    }


    @Override
    public String toString() {
        String str = "0x";
        for (byte b:bytes) {
            str += String.format("%02x", b);
        }
        return str;
    }
}
