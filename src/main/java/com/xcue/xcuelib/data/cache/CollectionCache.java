package com.xcue.xcuelib.data.cache;

import java.util.Collection;

public abstract class CollectionCache<T extends Collection<?>> extends Cache<T> {
    protected CollectionCache(T startingValue, T emptyOne, T emptyTwo) {
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
