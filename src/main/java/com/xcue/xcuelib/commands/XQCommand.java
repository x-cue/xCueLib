package com.xcue.xcuelib.commands;

import com.xcue.xcuelib.XQPlugin;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Permission: Empty string = anyone can use it
 */
public abstract class XQCommand extends XQAbstractCommand implements CommandExecutor {
    private final Map<String, XQAbstractCommand> subCommandMap;

    public XQCommand(XQPlugin pl, String name) {
        super(pl, name);
        this.subCommandMap = new HashMap<>();
    }

    public final boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String @NotNull [] args) {
        String arg1 = "";
        if (args.length >= 1) {
            arg1 = args[0].toLowerCase();
        }
        XQAbstractCommand finalCmd = this.subCommandMap.getOrDefault(arg1, this);

        if (!finalCmd.hasPermission(sender)) {
            send(sender, getPermissionMsg());
            return false;
        }

        if (!finalCmd.isValidSender(sender)) {
            send(sender, getInvalidSenderMsg());
            return false;
        }

        // Check arg lengths
        if ((requiresMinArgs && args.length < minArgs) || (limitedArgs && args.length > maxArgs)) {
            send(sender, getUsageMsg());
            return false;
        }


        // Remove first arg if subcommand
        if (arg1.equals(finalCmd.name)) {
            args = Arrays.copyOfRange(args, 1, args.length);
        }

        // Execute!
        return dispatchByType(finalCmd, sender, args);
    }

    public void addCommand(XQSubCommand command) {
        this.subCommandMap.put(command.name.toLowerCase(), command);
    }

    private boolean dispatchByType(XQAbstractCommand cmd, CommandSender sender, String[] args) {
        if (!cmd.allowConsole && !cmd.allowPlayer) {
            throw new IllegalStateException(
                    String.format("Command '%s' must allow either console or player execution.", cmd.name)
            );
        }

        if (cmd.allowPlayer && cmd.allowConsole) return cmd.onDispatch(sender, args);
        if (cmd.allowPlayer && sender instanceof Player) return cmd.onPlayerDispatch(sender, args);
        if (cmd.allowConsole && sender instanceof ConsoleCommandSender) return cmd.onConsoleDispatch(sender, args);

        return false;
    }

    protected abstract TextComponent getPermissionMsg();
    protected abstract TextComponent getInvalidSenderMsg();
    protected abstract TextComponent getUsageMsg();
}

