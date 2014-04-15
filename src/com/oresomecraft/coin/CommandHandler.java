package com.oresomecraft.coin;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler {
    OresomeCoin plugin;

    public CommandHandler(OresomeCoin pl) {
        plugin = pl;
    }

    @Command(aliases = {"transact"},
            usage = "<player> <amount>",
            desc = "Pays a player a certain amount of OresomeCoin",
            min = 1,
            max = 2)
    @CommandPermissions({"oresomecoin.transact"})
    public void transact(CommandContext args, CommandSender sender) {
        if (sender instanceof Player) {
            if (args.argsLength() == 2) {
                if (!args.getString(0).equals("") && !args.getString(0).equals(" ")) {
                    if (Bukkit.getPlayer(args.getString(0)) != null && Bukkit.getPlayer(args.getString(0)).isOnline()) {
                        if (Integer.parseInt(args.getString(1)) > 0) {
                            Transaction transaction = new Transaction((Player) sender, Bukkit.getPlayer(args.getString(0)), Integer.parseInt(args.getString(1)));
                            sender.sendMessage(SQLManager.executeTransaction(transaction));
                        } else {
                            sender.sendMessage(ChatColor.RED + "You can't pay somebody 0 coins!!");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "The player you're trying to pay doesn't seem to be online!");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Please enter a valid player name!");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Please specify a player and an amount to transact!");
            }
        }
    }
}
