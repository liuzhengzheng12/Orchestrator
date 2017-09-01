package org.netarch.util;

import java.util.*;

public class OrderedHashMap<T, R> extends AbstractMap<T, R> {
    private List<T> keys;
    private List<R> values;
    private Map<T, R> hashMap;

    public OrderedHashMap() {
        keys = new ArrayList<>();
        values = new ArrayList<>();
        hashMap = new HashMap<>();
    }

    @Override
    public R get(Object key) {
        return hashMap.get(key);
    }

    @Override
    public R put(T key, R value) {
        keys.add(key);
        values.add(value);
        return hashMap.put(key, value);
    }

    @Override
    public R putIfAbsent(T key, R value) {
        if(keys.contains(key)) {
            return null;
        }
        return put(key, value);
    }

    @Override
    public Set<Entry<T, R>> entrySet() {
        Set<Entry<T, R>> entrySet = new HashSet<>();

        for(int i = 0; i < keys.size(); i++) {
            entrySet.add(new OrderedHashMapEntry<>(keys.get(i), values.get(i)));
        }

        return entrySet;
    }

    public List<T> getKeys() {
        return keys;
    }

    public List<R> getValues() {
        return values;
    }

    public R getValue(int x) {
        return values.get(x);
    }

    @Override
    public Set<T> keySet() {
        Set<T> set = new HashSet<>();
        for(T key:keys) {
            set.add(key);
        }
        return set;
    }

    public T getKey(int x) {
        return keys.get(x);
    }

    public class OrderedHashMapEntry<T, R> implements Entry<T, R> {
        private T key;
        private R value;

        public OrderedHashMapEntry(T t, R r) {
            super();
            this.key = t;
            this.value = r;
        }

        @Override
        public T getKey() {
            return key;
        }

        @Override
        public R getValue() {
            return value;
        }

        @Override
        public R setValue(R value) {
            return this.value = value;
        }

        @Override
        public int hashCode() {
            return this.key.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof OrderedHashMapEntry)) {
                return false;
            }
            OrderedHashMapEntry entry = (OrderedHashMapEntry)o;
            if (entry.getKey() == this.key && entry.getValue() == this.value) {
                return true;
            }
            return false;
        }
    }

}
