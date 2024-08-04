package com.xcue.xcuelib.configuration.sources;

import com.xcue.xcuelib.configuration.ConfigSource;
import com.xcue.xcuelib.exceptions.Exception;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class XmlConfigSource extends ConfigSource {
    private final Plugin plugin;
    private final String pathFromResources;
    private final Map<String, String> source;

    public XmlConfigSource(@NonNull Plugin plugin, @NonNull String pathFromResources) {
        this.plugin = plugin;
        this.pathFromResources = pathFromResources;
        this.source = new HashMap<>();
    }

    @Override
    public <T> T getObject(@NonNull String path, @NonNull Class<T> clazz) throws ClassCastException {
        return (T) source.get(path);
    }

    @Override
    public <T> T getObject(@NonNull String path, @NonNull Class<T> clazz, T def) {
        T value = (T) source.getOrDefault(path, null);

        return value == null ? def : value;
    }

    @Override
    public void reload() {
        try (InputStream stream = plugin.getClass().getResourceAsStream(pathFromResources)) {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(stream);
            doc.getDocumentElement().normalize();

            NodeList values = doc.getElementsByTagName("value");

            Node node;
            for (int i = 0; i < values.getLength(); i++) {
                node = values.item(i);

                String key = node.getAttributes().getNamedItem("key").getNodeValue();
                String value = node.getTextContent();

                source.put(key, value);

                if (value == null || value.isBlank() || value.isEmpty()) {
                    plugin.getLogger().warning(key + " value is null in " + pathFromResources);
                }
            }

        } catch (SAXException | ParserConfigurationException | IOException ex) {
            Exception.useStackTrace(plugin.getLogger()::warning, ex);
        }
    }

    @Override
    public boolean isSet(@NonNull String path) {
        return source.containsKey(path);
    }
}
