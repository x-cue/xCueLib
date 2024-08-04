package com.xcue.xcuelib.data.cache;

import java.util.Map;

public abstract class MapCache<T extends Map<?, ?>> extends Cache<T> {
    protected MapCache(T startingValue, T emptyOne, T emptyTwo) {
        super(startingValue, emptyOne, emptyTwo);
    }

    @Override
    protected void clearUpdateCache(boolean key) {
        updateCache.get(key).clear();
    }

    @Override
    public void dispose() {
        updateCache.clear();
        cache.clear();
    }
}
