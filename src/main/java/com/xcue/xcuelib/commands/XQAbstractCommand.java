package com.xcue.xcuelib.commands;

import com.xcue.xcuelib.XQPlugin;
import com.xcue.xcuelib.configuration.Config;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public abstract class XQAbstractCommand {
    protected final Config config;
    protected final XQPlugin plugin;
    protected final String name;
    protected String description;
    protected String usage;
    protected boolean allowConsole;
    protected boolean allowPlayer;
    protected boolean allowOpOverride;
    protected boolean requiresMinArgs;
    protected int minArgs;
    protected int maxArgs;
    protected boolean limitedArgs;

    public XQAbstractCommand(XQPlugin plugin, String name) {
        this.plugin = plugin;
        this.config = plugin.getConfigs();
        this.name = name;
    }

    public abstract String getPermission();

    protected final boolean hasPermission(CommandSender sender) {
        String p = getPermission();
        return (p.isEmpty()) || (allowOpOverride && sender.isOp()) || sender.hasPermission(getPermission());
    }

    public boolean onConsoleDispatch(CommandSender sender, String[] args) {
        throw new NotImplementedException();
    }

    public boolean onPlayerDispatch(CommandSender sender, String[] args) {
        throw new NotImplementedException();
    }

    public boolean onDispatch(CommandSender sender, String[] args) {
        throw new NotImplementedException();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public void setRequiresMinArgs(boolean val) {
        this.requiresMinArgs = val;
    }

    public void setLimitedArgs(boolean val) {
        this.limitedArgs = val;
    }

    public void setMaxArgs(int limit) {
        this.maxArgs = limit;
    }

    public void setMinArgs(int amount) {
        this.minArgs = amount;
    }

    protected final boolean isValidSender (CommandSender sender) {
        boolean validConsole = this.allowConsole && sender instanceof ConsoleCommandSender;
        boolean validPlayer = this.allowPlayer && sender instanceof Player;

        return validConsole || validPlayer;
    }

    protected void sendMsg(CommandSender sender, String msg) {
        this.send(sender, LegacyComponentSerializer.legacyAmpersand().deserialize(msg));
    }

    protected void send(CommandSender sender, TextComponent msg) {
        sender.sendMessage(msg);
    }

    protected abstract TextComponent getPermissionMsg();
    protected abstract TextComponent getInvalidSenderMsg();
    protected abstract TextComponent getUsageMsg();
//    		super("build",plugin);
//    setDescription("Spawn in a building.");
//    setUsage("/build");
//    setRequiresMinArgs(true);
//    setLimitedArgs(false);
//    setMinArgs(1);
//    setSupportsConsole(false);
//    setSupportsPlayers(true);
//    setAllowsOpOverride(false);
}

