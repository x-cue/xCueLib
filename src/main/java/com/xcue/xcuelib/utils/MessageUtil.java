package com.xcue.xcuelib.utils;

import com.xcue.xcuelib.configuration.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.ArrayList;
import java.util.List;

public class MessageUtil {
    private static Config cfg;

    public static void setCfg(Config newCfg) {
        cfg = newCfg;
    }

    private static <T> T getCfg(String path, Class<T> clazz, T def) {
        return cfg.get(path, clazz, def);
    }

    public static TextComponent getMessage(String path) {
        return toCmp(getCfg(path, String.class, ""));
    }

    public static String getMessageStr(String path) {
        return getCfg(path, String.class, "");
    }

    public static List<TextComponent> getMessages(String path) {
        List<TextComponent> components = new ArrayList<>();
        //noinspection unchecked
        List<String> strings = new ArrayList<String>(getCfg(path, ArrayList.class, new ArrayList<>()));

        for (String str : strings) {
            components.add(toCmp(str));
        }

        return components;
    }

    public static List<String> getMessageStrs(String path) {
        //noinspection unchecked
        return new ArrayList<String>(getCfg(path, ArrayList.class, new ArrayList<>()));
    }

    public static String toStr(Component comp) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(comp);
    }

    public static TextComponent toCmp(String str) {
        return (TextComponent) normalizeItalics(LegacyComponentSerializer.legacyAmpersand().deserialize(str));
    }

    private static Component normalizeItalics(Component component) {
        // Only override if italics is not explicitly set
        if (component.decoration(TextDecoration.ITALIC) == TextDecoration.State.NOT_SET) {
            component = component.decoration(TextDecoration.ITALIC, false);
        }

        List<Component> newChildren = new ArrayList<>();
        for (Component child : component.children()) {
            newChildren.add(normalizeItalics(child));
        }

        return component.children(newChildren);
    }
}
