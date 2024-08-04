package com.xcue.xcuelib.data.cache;

import java.util.HashMap;
import java.util.Map;

public abstract class Cache<T> {
    protected boolean pointer;
    protected final T cache;
    protected final Map<Boolean, T> updateCache;

    public Cache(T defaultValues, T updateCacheOne, T updateCacheTwo) {
        cache = defaultValues;
        updateCache = new HashMap<>() {{
            put(true, updateCacheOne);
            put(false, updateCacheTwo);
        }};
    }

    /**
     * Should reset the current updateCache to its default state
     *
     * @param key Pointer
     */
    protected abstract void clearUpdateCache(boolean key);

    /**
     * Should immediately discard all data
     */
    protected abstract void dispose();

    /**
     * Swaps the updateCache that updates are sent to, and clears the other one.
     */
    public final void switchPointer() {
        pointer = !pointer;
        clearUpdateCache(!pointer);
    }

    /**
     * @return A clone of the current updatesCache
     */
    public abstract T getUpdatesClone();

    /**
     *
     * @param swapPointer Whether to swap the pointer
     * @return Clone of the current updatesCache
     */
    public final synchronized T getUpdates(boolean swapPointer) {
        if (swapPointer) {
            T clone = getUpdatesClone();
            switchPointer();
            return clone;
        }

        return updateCache.get(pointer);
    }

    public boolean getPointer() {
        return pointer;
    }

    public T getCache() {
        return cache;
    }
}
