package com.xcue.xcuelib;

import com.xcue.xcuelib.configuration.Config;
import org.bukkit.event.Listener;

public class XQListener implements Listener {
    protected final XQPlugin main;
    protected final Config config;

    public XQListener(XQPlugin main) {
        this.main = main;
        this.config = main.getConfigs();
    }
}
