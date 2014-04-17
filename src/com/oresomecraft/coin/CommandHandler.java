package com.oresomecraft.coin;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.UUID;

public class CommandHandler implements Listener {
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
                            Player initiator = (Player) sender;
                            Transaction transaction = new Transaction(OresomeCoin.onlineWallets.get(initiator.getUniqueId().toString()), OresomeCoin.onlineWallets.get(Bukkit.getPlayer(args.getString(0)).getUniqueId().toString()), Integer.parseInt(args.getString(1)));
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

    @Command(aliases = {"givecoins"},
            usage = "<player> <amount>",
            desc = "Pays a player a certain amount of OresomeCoin from the OresomeCraft bank",
            min = 1,
            max = 2)
    @CommandPermissions({"oresomecoin.givecoins"})
    public void giveCoins(CommandContext args, CommandSender sender) {
        if (sender instanceof Player) {
            if (args.argsLength() == 2) {
                if (!args.getString(0).equals("") && !args.getString(0).equals(" ")) {
                    if (Bukkit.getPlayer(args.getString(0)) != null && Bukkit.getPlayer(args.getString(0)).isOnline()) {
                        if (Integer.parseInt(args.getString(1)) > 0) {
                            int amount = Integer.parseInt(args.getString(1));
                            Wallet toWallet = OresomeCoin.onlineWallets.get(Bukkit.getPlayer(args.getString(1)).getUniqueId().toString());
                            SQLManager.giveCoins(toWallet, amount);
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
