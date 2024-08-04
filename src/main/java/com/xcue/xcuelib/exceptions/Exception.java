package com.xcue.xcuelib.exceptions;

import java.util.function.Consumer;

public class Exception {
    /**
     *
     * @param callback Logger/callback that will do something with the formatted exception
     * @param ex Exception to format
     */
    public static void useStackTrace(Consumer<String> callback, java.lang.Exception ex) {
        StringBuilder sb = new StringBuilder(ex.getMessage());

        for (StackTraceElement line : ex.getStackTrace()) {
            sb.append(String.format("\n          at %s", line.toString()));
        }

        callback.accept(sb.toString());
    }
}