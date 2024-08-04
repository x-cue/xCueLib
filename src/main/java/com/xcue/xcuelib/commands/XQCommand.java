package com.xcue.xcuelib.commands;

import com.xcue.xcuelib.XQPlugin;
import com.xcue.xcuelib.configuration.Config;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;

public abstract class XQCommand implements CommandExecutor {
    protected final XQPlugin main;
    protected final Config config;
    public XQCommand(XQPlugin main) {
        this.main = main;
        this.config = main.getConfigs();
    }
    protected abstract String getPermissionToReload();

    @Override
    public final boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        return executeCommand(commandSender, command, s, strings);
    }

    public abstract boolean executeCommand(CommandSender sender, Command cmd, String label, String[] args);

    protected final boolean reload(CommandSender sender, String successMsg, String permissionMsg) {
        if (!hasPermission(sender, getPermissionToReload(), permissionMsg)) {
            return false;
        }

        this.main.onReload();

        sender.sendMessage(successMsg);

        return true;
    }

    protected final boolean hasPermission(@Nonnull CommandSender sender, @Nonnull String permission, String msg) {
        if (sender.hasPermission(permission))
            return true;

        sender.sendMessage(msg);

        return false;
    }
}
