package com.xcue.xcuelib.configuration;

public class ConfigValue<T> {
    private final T value;

    public ConfigValue(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }
}
