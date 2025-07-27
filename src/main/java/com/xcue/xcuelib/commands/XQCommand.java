package com.xcue.xcuelib.commands;

import com.xcue.xcuelib.XQPlugin;
import com.xcue.xcuelib.utils.TabCompleterUtils;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Permission: Empty string = anyone can use it
 */
public abstract class XQCommand extends XQAbstractCommand implements CommandExecutor, TabCompleter {
    private final Map<String, XQAbstractCommand> subCommandMap;

    public XQCommand(XQPlugin pl, String name) {
        super(pl, name);
        this.subCommandMap = new HashMap<>();
    }

    public final boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s,
                                   String @NotNull [] args) {
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

        // Remove first arg if subcommand
        if (arg1.equals(finalCmd.name)) {
            args = Arrays.copyOfRange(args, 1, args.length);
        }

        // Check arg lengths
        if ((finalCmd.requiresMinArgs && args.length < finalCmd.minArgs) || (finalCmd.limitedArgs && args.length > finalCmd.maxArgs)) {
            send(sender, getUsageMsg());
            return false;
        }

        // Execute!
        switch (dispatchByType(finalCmd, sender, args)) {
            case SUCCESS:
                return true;
            case FAILURE:
                return false;
            case USAGE:
                send(sender, getUsageMsg());
                break;
            case SENDER:
                send(sender, getInvalidSenderMsg());
                break;
            case PERMISSION:
                send(sender, getPermissionMsg());
                break;
            default:
                break;
        }

        return false;
    }

    public void addCommand(XQSubCommand command) {
        this.subCommandMap.put(command.name.toLowerCase(), command);
    }

    private CommandResult dispatchByType(XQAbstractCommand cmd, CommandSender sender, String[] args) {
        if (!cmd.allowConsole && !cmd.allowPlayer) {
            throw new IllegalStateException(
                    String.format("Command '%s' must allow either console or player execution.", cmd.name)
            );
        }

        if (cmd.allowPlayer && cmd.allowConsole) return cmd.onDispatch(sender, args);
        if (cmd.allowPlayer && sender instanceof Player p) return cmd.onPlayerDispatch(p, args);
        if (cmd.allowConsole && sender instanceof ConsoleCommandSender c) return cmd.onConsoleDispatch(c, args);

        return CommandResult.FAILURE;
    }

    protected abstract TextComponent getPermissionMsg();

    protected abstract TextComponent getInvalidSenderMsg();

    protected abstract TextComponent getUsageMsg();

    @Override
    public final List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label,
                                            String @NotNull [] args) {
        if (args.length == 0) {
            return Collections.emptyList();
        }

        // No permission
        if (!hasPermission(sender)) {
            return TabCompleterUtils.matchOnlinePlayers(args[0], true);
        }

        String arg1 = args[0].toLowerCase();
        if (args.length == 1) {
            List<String> arg1Suggestions = new ArrayList<>();

            // Match subcommands with arg1
            for (XQAbstractCommand sub : subCommandMap.values()) {
                if (sub.hasPermission(sender)) {
                    arg1Suggestions.add(sub.name);
                }
            }

            // Add base command completion for arg1 as well
            arg1Suggestions.addAll(onTabComplete(sender, args));

            return TabCompleterUtils.filterMatches(arg1, arg1Suggestions, true);
        }

        // Delegate actual tab completion
        XQAbstractCommand finalCmd = this.subCommandMap.getOrDefault(arg1, this);
        if (finalCmd instanceof XQCommand) {
            // Base Command
            return onTabComplete(sender, args);
        } else if (finalCmd.hasPermission(sender)) {
            // Sub command
            return finalCmd.onTabComplete(sender, Arrays.copyOfRange(args, 1, args.length));
        }

        return Collections.emptyList();
    }
}

