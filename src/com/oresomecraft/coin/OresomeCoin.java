package com.oresomecraft.coin;

import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.*;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class OresomeCoin extends JavaPlugin {

    private static OresomeCoin instance;
    public static Map<String, Wallet> onlineWallets = new HashMap<String, Wallet>();

    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
            saveDefaultConfig();
        }

        if (getConfig() == null) {
            saveDefaultConfig();
        }

        instance = this;
        SQLManager.setupDatabase();
        registerCommands();
        instance.getServer().getPluginManager().registerEvents(new SQLManager(), this);
    }

    public void onDisable() {
        saveConfig();
        for (Wallet wallet : onlineWallets.values()) {
            wallet.writeToDatabase();
        }
    }

    public static OresomeCoin getInstance() {
        return instance;
    }

    /**
     * *******************************************************************
     * Code to use for sk89q's command framework goes below this comment! *
     * ********************************************************************
     */

    private CommandsManager<CommandSender> commands;
    private boolean opPermissions;

    private void registerCommands() {
        final OresomeCoin plugin = this;
        // Register the commands that we want to use
        commands = new CommandsManager<CommandSender>() {
            @Override
            public boolean hasPermission(CommandSender player, String perm) {
                return plugin.hasPermission(player, perm);
            }
        };
        commands.setInjector(new SimpleInjector(this));
        final CommandsManagerRegistration cmdRegister = new CommandsManagerRegistration(this, commands);

        cmdRegister.register(CommandHandler.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            commands.execute(cmd.getName(), args, sender, sender);
        } catch (CommandPermissionsException ex) {
            sender.sendMessage(ChatColor.RED + "You don't have permission.");
        } catch (MissingNestedCommandException ex) {
            sender.sendMessage(ChatColor.RED + ex.getUsage());
        } catch (CommandUsageException ex) {
            sender.sendMessage(ChatColor.RED + ex.getMessage());
            sender.sendMessage(ChatColor.RED + ex.getUsage());
        } catch (WrappedCommandException ex) {
            if (ex.getCause() instanceof NumberFormatException) {
                sender.sendMessage(ChatColor.RED + "Number expected, string received instead.");
            } else {
                sender.sendMessage(ChatColor.RED + "An error has occurred. See console.");
                ex.printStackTrace();
            }
        } catch (CommandException ex) {
            sender.sendMessage(ChatColor.RED + ex.getMessage());
        }

        return true;
    }

    public boolean hasPermission(CommandSender sender, String perm) {
        if (!(sender instanceof Player)) {
            if (sender.hasPermission(perm)) {
                return ((sender.isOp() && (opPermissions || sender instanceof ConsoleCommandSender)));
            }
        }
        return hasPermission(sender, ((Player) sender).getWorld(), perm);
    }

    public boolean hasPermission(CommandSender sender, World world, String perm) {
        return ((sender.isOp() && opPermissions) || sender instanceof ConsoleCommandSender || sender.hasPermission(perm));
    }

    public void checkPermission(CommandSender sender, String perm)
            throws CommandPermissionsException {
        if (!hasPermission(sender, perm)) {
            throw new CommandPermissionsException();
        }
    }

    public void checkPermission(CommandSender sender, World world, String perm)
            throws CommandPermissionsException {
        if (!hasPermission(sender, world, perm)) {
            throw new CommandPermissionsException();
        }
    }
}
