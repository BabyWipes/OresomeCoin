package com.oresomecraft.coin;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

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
                            String successMessage = SQLOperations.executeTransaction(transaction);
                            sender.sendMessage(successMessage);
                            if ((!successMessage.contains(ChatColor.RED + "You don't have enough OresomeCoin to carry out this transaction!")) && (!successMessage.contains(ChatColor.RED + "The player you're attempting to pay doesn't seem to be online!")) && (!successMessage.contains(ChatColor.RED + "You can't pay yourself!"))) {
                                Bukkit.getPlayer(args.getString(0)).sendMessage(ChatColor.GREEN + "You received " + transaction.getAmount() + " OresomeCoins from " + initiator.getDisplayName());
                            }
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
                        if (!args.getString(0).equals(sender.getName()) || args.getString(0).equals(((Player) sender).getDisplayName())) {
                            if (Integer.parseInt(args.getString(1)) > 0) {
                                int amount = Integer.parseInt(args.getString(1));
                                if (OresomeCoin.onlineWallets.get(Bukkit.getPlayer(args.getString(0)).getUniqueId().toString()) != null) {
                                    Wallet toWallet = OresomeCoin.onlineWallets.get(Bukkit.getPlayer(args.getString(0)).getUniqueId().toString());
                                    SQLOperations.giveCoins(toWallet, amount);
                                    Bukkit.getPlayer(args.getString(0)).sendMessage(ChatColor.GREEN + "You just received " + amount + " OresomeCoins!");
                                } else {
                                    plugin.getLogger().warning("An error occured while trying to fetch a player's wallet from the locally stored wallets!");
                                    sender.sendMessage(ChatColor.RED + "The player you're trying to pay doesn't seem to be online!");
                                }
                            } else {
                                sender.sendMessage(ChatColor.RED + "You can't pay somebody 0 coins!!");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "You can't pay yourself!");
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
        } else {
            sender.sendMessage("You must be a player to use this command!");
        }
    }

    @Command(aliases = {"coins"},
            usage = "<player>",
            desc = "Checks how many OresomeCoins a player has",
            min = 0,
            max = 1)
    @CommandPermissions({"oresomecoin.checkcoins"})
    public void checkCoins(CommandContext args, CommandSender sender) {
        if (args.argsLength() == 1 && sender.hasPermission("oresomecoin.checkcoins.other") && !args.getString(0).equals(sender.getName())) {
            if (!args.getString(0).equals("") && !args.getString(0).equals(" ")) {
                if (isOnline(args.getString(0))) {
                    int balance = (int) OresomeCoin.onlineWallets.get(Bukkit.getPlayer(args.getString(0)).getUniqueId().toString()).getBalance();
                    sender.sendMessage(ChatColor.AQUA + args.getString(0) + " has " + balance + " coins!");
                } else {
                    sender.sendMessage(ChatColor.RED + "The player you're trying to look up doesn't seem to be online!");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Please enter a valid player name!");
            }
        } else if (args.argsLength() == 0 || args.getString(0).equals(sender.getName())) {
            if (sender instanceof Player) {
                int balance = (int) OresomeCoin.onlineWallets.get(((Player) sender).getUniqueId().toString()).getBalance();
                sender.sendMessage(ChatColor.AQUA + "You have " + balance + " coins!");
            } else {
                sender.sendMessage("You must be a player to use this command!");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You don't have permission to check another player's balance!");
        }
    }

    public boolean isOnline(String playerName) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            return playerName.equals(player.getName());
        }
        return false;
    }
}
