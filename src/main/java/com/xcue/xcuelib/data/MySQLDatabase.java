package com.xcue.xcuelib.data;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

public abstract class MySQLDatabase {
    private String host;
    private int port;
    private String database;
    private String username;
    private String password;
    private HikariDataSource hikari;
    private final Plugin main;

    protected MySQLDatabase(@NonNull Plugin main, @NonNull final ConfigurationSection DB_CONFIG) throws SQLException {
        this.main = main;
        main.getLogger().info("Validating database configuration...");

        this.validateConfig(DB_CONFIG);

        main.getLogger().info("Connecting to database " + this.database + "...");

        this.connect();
        this.tryCreateTables();

        if (useConfigXml()) {
            try {
                insertConfigurationTables();
            } catch (IOException | SQLException ex) {
                throw new SQLException(ex);
            }
        }

        main.getLogger().info("Connected!");
    }

    /**
     * Should return a static int. Is used in connect() to set a hikari setting.
     *
     * @return Maximum amount of connections
     */
    protected abstract int getMaxPoolSize();

    /**
     * Should return a static int. Is used in connect() to set a hikari setting.
     *
     * @return Minimum amount of pools to be kept open and waiting
     */
    protected abstract int getMinIdlePools();

    /**
     * Should return a static int. Is used in connect() to set a hikari setting.
     *
     * @return Max lifetime of every connection in the pool
     */
    protected abstract int getMaxLifetime();

    /**
     * Should execute "CREATE IF NOT EXISTS" for every database table
     * Executes automatically when instantiating the class
     */
    protected abstract void tryCreateTables() throws SQLException;

    private void validateConfig(@NonNull final ConfigurationSection DB_CONFIG) throws NullPointerException {
        if (!DB_CONFIG.isSet("host")) {
            throw new NullPointerException("Must define host in the ConfigurationSection");
        }
        if (!DB_CONFIG.isSet("port")) {
            throw new NullPointerException("Must define port in the ConfigurationSection");
        }
        if (!DB_CONFIG.isSet("database")) {
            throw new NullPointerException("Must define database in the ConfigurationSection");
        }

        if (!DB_CONFIG.isSet("username")) {
            throw new NullPointerException("Must define username in the ConfigurationSection");
        }

        this.host = DB_CONFIG.getString("host");
        this.port = DB_CONFIG.getInt("port");
        this.database = DB_CONFIG.getString("database");
        this.username = DB_CONFIG.getString("username");
        this.password = DB_CONFIG.isSet("password") ? DB_CONFIG.getString("password") : null;
    }

    /**
     * Attempts to establish a connection to the my-sql database
     *
     * @throws SQLException
     */
    private void connect() throws SQLException {
        this.hikari = new HikariDataSource();
        this.hikari.setDataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource");
        this.hikari.addDataSourceProperty("serverName", this.host);
        this.hikari.addDataSourceProperty("port", this.port);
        this.hikari.addDataSourceProperty("databaseName", this.database);
        this.hikari.addDataSourceProperty("user", this.username);
        this.hikari.addDataSourceProperty("password", this.password);
        this.hikari.setMaximumPoolSize(this.getMaxPoolSize());
        this.hikari.setMinimumIdle(this.getMinIdlePools());
        this.hikari.setMaxLifetime(this.getMaxLifetime());
        this.hikari.setConnectionTestQuery("SELECT 1");
    }

    public boolean isConnected() {
        return this.hikari != null;
    }

    public HikariDataSource getHikari() {
        return this.hikari;
    }

    /**
     * Terminates all connections in the pool. Should be run in the onDisable()
     */
    public void disconnect() {
        if (isConnected()) {
            this.hikari.close();
        }
    }

    /**
     * @return Whether to insert values from config.xml automatically
     */
    protected abstract boolean useConfigXml();

    /**
     * Inserts config values from resource `config.xml` into the database
     */
    private void insertConfigurationTables() throws IOException, SQLException {
        try (Connection conn = getHikari().getConnection()) {
            Queue<PreparedStatement> statements = new LinkedList<>();
            // TODO: Extract to io util
            try (InputStream stream = main.getClass().getResourceAsStream("/config.xml")) {
                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document doc = builder.parse(stream);
                doc.getDocumentElement().normalize();

                NodeList tables = doc.getElementsByTagName("table"); // We know these are all Element Nodes
                for (int i = 0; i < tables.getLength(); i++) {
                    Node table = tables.item(i);

                    // Process table
                    NamedNodeMap attributes = table.getAttributes();
                    String tableName = attributes.getNamedItem("name").getNodeValue();

                    // CREATE PREPARED STATEMENT
                    StringBuilder sb = new StringBuilder("INSERT INTO ");
                    sb.append(tableName);
                    sb.append(" (");

                    // Get First Row
                    NodeList rows = table.getChildNodes();
                    Node row = null;
                    for (int j = 0; j < rows.getLength(); j++) {
                        row = rows.item(j);

                        if (row.getNodeType() == Node.ELEMENT_NODE) {
                            break;
                        }
                    }

                    // Process Columns in row
                    NodeList columns = row.getChildNodes();
                    int values = 0;
                    for (int j = 0; j < columns.getLength(); j++) {
                        Node column = columns.item(j);

                        if (column.getNodeType() == Node.ELEMENT_NODE) {
                            sb.append(column.getNodeName());
                            sb.append(", ");
                            values++;
                        }
                    }
                    sb.setCharAt(sb.lastIndexOf(","), ')');
                    sb.append("VALUES (");
                    sb.append("?, ".repeat(values - 1));
                    sb.append("?);");

                    PreparedStatement batch = conn.prepareStatement(sb.toString());
                    // END OF PREPARED STATEMENT

                    // BEGIN BATCH INSERT
                    for (int j = 0; j < rows.getLength(); j++) {
                        int missingValues = values;
                        row = rows.item(j);
                        if (row.getNodeType() == Node.ELEMENT_NODE) {
                            columns = row.getChildNodes();
                            for (int k = 0; k < columns.getLength() && missingValues > 0; k++) {
                                Node column = columns.item(k);

                                if (column.getNodeType() == Node.ELEMENT_NODE) {
                                    String value = column.getTextContent();
                                    switch (value) {
                                        case "False", "false" -> batch.setBoolean(values - missingValues + 1, false);
                                        case "True", "true" -> batch.setBoolean(values - missingValues + 1, true);
                                        default -> batch.setObject(values - missingValues + 1, value);
                                    }

                                    missingValues--;
                                }
                            }
                        } else {
                            continue;
                        }

                        batch.addBatch();
                    }
                    // END BATCH INSERT

                    statements.add(batch);
                }
            } catch (ParserConfigurationException | SAXException ex) {
                throw new IOException();
            }

            while (statements.peek() != null) {
                PreparedStatement statement = statements.poll();
                statement.executeBatch();
            }
        }
    }
}


