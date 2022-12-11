package com.raveq.coinmanager.commands;

import com.raveq.coinmanager.CoinManager;
import com.raveq.coinmanager.mysql.CoinAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CoinsCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player player) {
            if(args.length == 0) {
                String pattern = "#,###,###";
                DecimalFormat coinsFormat = new DecimalFormat(pattern);
                int coins = CoinAPI.getCoins(player.getUniqueId().toString(), player.getName());
                coinsFormat.format(coins);
                player.sendMessage(String.format(CoinManager.prefix + "§7Deine Coins §8● §e" + coinsFormat.format(coins)));
            } else if(args.length == 3) {
                if(args[1].equalsIgnoreCase("add")) {
                    if (!player.hasPermission("raveq.coinmanager.add")) {
                        player.sendMessage(CoinManager.noperm);
                        return false;
                    }
                    if (!args[2].matches("\\d*")) {
                        player.sendMessage(CoinManager.prefix + "§cUngültige Zahl!");
                        return false;
                    }
                    String pattern = "#,###,###";
                    DecimalFormat amountFormat = new DecimalFormat(pattern);
                    Integer amount = Integer.parseInt(args[2]);
                    amountFormat.format(amount);
                    if (amount < 0) {
                        player.sendMessage(CoinManager.prefix + "§cUngültige Zahl!");
                        return false;
                    }
                    if (amount == 0) {
                        player.sendMessage(CoinManager.prefix + "§cUngültige Zahl!");
                        return false;
                    }
                    if(Bukkit.getOnlinePlayers().contains(Bukkit.getServer().getPlayer(args[0]))) {
                        Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
                        String targetUUID = targetPlayer.getUniqueId().toString();
                        if(!CoinAPI.playerExists(targetUUID)) {
                            player.sendMessage(CoinManager.prefix + "§cDieser Spieler konnte nicht gefunden werden.");
                            return false;
                        }
                        CoinAPI.addCoins(targetUUID, targetPlayer.getName(), amount);
                        CoinManager.sendtoDiscord("Der Spieler **" + player.getName() + "** hat **" + targetPlayer.getName() + "** insgesamt **" + amountFormat.format(amount) + "** Coins hinzugefügt", CoinManager.coinsURL);
                        player.sendMessage(CoinManager.prefix + "§7Du hast §e" + targetPlayer.getName() + " §7insgesamt §e" + amountFormat.format(amount) + " §7Coins hinzugefügt!");
                        targetPlayer.sendMessage(CoinManager.prefix + "§7Dir wurden von §e" + player.getName() + " §7insgesamt §e" + amountFormat.format(amount) + " §7Coins hinzugefügt!");
                    } else {
                        OfflinePlayer offlineTargetPlayer = Bukkit.getServer().getOfflinePlayer(args[0]);
                        if(!offlineTargetPlayer.hasPlayedBefore()) {
                            player.sendMessage(CoinManager.prefix + "§cDieser Spieler konnte nicht gefunden werden.");
                            return false;
                        }
                        String offlineTargetUUID = offlineTargetPlayer.getUniqueId().toString();
                        if(!CoinAPI.playerExists(offlineTargetUUID)) {
                            player.sendMessage(CoinManager.prefix + "§cDieser Spieler konnte nicht gefunden werden.");
                            return false;
                        }
                        CoinManager.sendtoDiscord("Der Spieler **" + player.getName() + "** hat **" + offlineTargetPlayer.getName() + "** insgesamt **" + amountFormat.format(amount) + "** Coins hinzugefügt", CoinManager.coinsURL);
                        CoinAPI.addCoins(offlineTargetUUID, offlineTargetPlayer.getName(), amount);
                        player.sendMessage(CoinManager.prefix + "§7Du hast §e" + offlineTargetPlayer.getName() + " §7insgesamt §e" + amountFormat.format(amount) + " §7Coins hinzugefügt!");
                    }
                } else if(args[1].equalsIgnoreCase("remove")) {
                    if (!player.hasPermission("raveq.coinmanager.remove")) {
                        player.sendMessage(CoinManager.noperm);
                        return false;
                    }
                    if (!args[2].matches("\\d*")) {
                        player.sendMessage(CoinManager.prefix + "§cUngültige Zahl!");
                        return false;
                    }
                    String pattern = "#,###,###";
                    DecimalFormat amountFormat = new DecimalFormat(pattern);
                    Integer amount = Integer.parseInt(args[2]);
                    amountFormat.format(amount);
                    if(Bukkit.getOnlinePlayers().contains(Bukkit.getServer().getPlayer(args[0]))) {
                        Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
                        String targetUUID = targetPlayer.getUniqueId().toString();
                        if(!CoinAPI.playerExists(targetUUID)) {
                            player.sendMessage(CoinManager.prefix + "§cDieser Spieler konnte nicht gefunden werden.");
                            return false;
                        }
                        Integer coins = CoinAPI.getCoins(targetUUID, targetPlayer.getName());
                        if(coins - amount < 0) {
                            String pattern2 = "#,###,###";
                            DecimalFormat coinsFormat = new DecimalFormat(pattern2);
                            Integer coins2 = CoinAPI.getCoins(targetUUID, targetPlayer.getName());
                            coinsFormat.format(coins2);
                            player.sendMessage(CoinManager.prefix + "§cDu kannst nicht ins Negative gehen!");
                            player.sendMessage(CoinManager.prefix + "§7Kontostand dieses Spielers §8● §e" + coinsFormat.format(coins2));
                            return false;
                        }
                        if(amount == 0) {
                            player.sendMessage(CoinManager.prefix + "§cUngültige Zahl!");
                            return false;
                        }
                        CoinAPI.removeCoins(targetUUID, targetPlayer.getName(), amount);
                        CoinManager.sendtoDiscord("Der Spieler **" + player.getName() + "** hat **" + targetPlayer.getName() + "** insgesamt **" + amountFormat.format(amount) + "** Coins entfernt", CoinManager.coinsURL);
                        player.sendMessage(CoinManager.prefix + "§7Du hast §e" + targetPlayer.getName() + " §7insgesamt §e" + amountFormat.format(amount) + " §7Coins entfernt!");
                        targetPlayer.sendMessage(CoinManager.prefix + "§7Dir wurden von §e" + player.getName() + " §7insgesamt §e" + amountFormat.format(amount) + " §7Coins entfernt!");
                    } else {
                        OfflinePlayer offlineTargetPlayer = Bukkit.getServer().getOfflinePlayer(args[0]);
                        String offlineTargetUUID = offlineTargetPlayer.getUniqueId().toString();
                        if(!offlineTargetPlayer.hasPlayedBefore()) {
                            player.sendMessage(CoinManager.prefix + "§cDieser Spieler konnte nicht gefunden werden.");
                            return false;
                        }
                        if(!CoinAPI.playerExists(offlineTargetUUID)) {
                            player.sendMessage(CoinManager.prefix + "§cDieser Spieler konnte nicht gefunden werden.");
                            return false;
                        }
                        String pattern2 = "#,###,###";
                        DecimalFormat coinsFormat = new DecimalFormat(pattern2);
                        Integer coins = CoinAPI.getCoins(offlineTargetUUID, offlineTargetPlayer.getName());
                        coinsFormat.format(coins);
                        if(coins - amount < 0) {
                            player.sendMessage(CoinManager.prefix + "§cDu kannst nicht ins Negative gehen!");
                            player.sendMessage(CoinManager.prefix + "§7Kontostand dieses Spielers §8● §e" + coinsFormat.format(coins));
                            return false;
                        }
                        if(amount == 0) {
                            player.sendMessage(CoinManager.prefix + "§cUngültige Zahl!");
                            return false;
                        }
                        CoinAPI.removeCoins(offlineTargetUUID, offlineTargetPlayer.getName(), amount);
                        CoinManager.sendtoDiscord("Der Spieler **" + player.getName() + "** hat **" + offlineTargetPlayer.getName() + "** insgesamt **" + amountFormat.format(amount) + "** Coins entfernt", CoinManager.coinsURL);
                        player.sendMessage(CoinManager.prefix + "§7Du hast §e" + offlineTargetPlayer.getName() + " §7insgesamt §e" + amountFormat.format(amount) + " §7Coins entfernt!");
                    }
                } else if(args[1].equalsIgnoreCase("set")) {
                    if (!player.hasPermission("raveq.coinmanager.set")) {
                        player.sendMessage(CoinManager.noperm);
                        return false;
                    }
                    if (!args[2].matches("\\d*")) {
                        player.sendMessage(CoinManager.prefix + "§cUngültige Zahl!");
                        return false;
                    }
                    String pattern = "#,###,###";
                    DecimalFormat amountFormat = new DecimalFormat(pattern);
                    Integer amount = Integer.parseInt(args[2]);
                    amountFormat.format(amount);
                    if (amount < 0) {
                        player.sendMessage(CoinManager.prefix + "§cUngültige Zahl!");
                        return false;
                    }
                    if (amount == 0) {
                        player.sendMessage(CoinManager.prefix + "§cUngültige Zahl!");
                        return false;
                    }
                    if(Bukkit.getOnlinePlayers().contains(Bukkit.getServer().getPlayer(args[0]))) {
                        Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
                        String targetUUID = targetPlayer.getUniqueId().toString();
                        if(!CoinAPI.playerExists(targetUUID)) {
                            player.sendMessage(CoinManager.prefix + "§cDieser Spieler konnte nicht gefunden werden.");
                            return false;
                        }
                        CoinAPI.setCoins(targetUUID, targetPlayer.getName(), amount);
                        CoinManager.sendtoDiscord("Der Spieler **" + player.getName() + "** hat **" + targetPlayer.getName() + "** insgesamt **" + amountFormat.format(amount) + "** Coins gesetzt", CoinManager.coinsURL);
                        player.sendMessage(CoinManager.prefix + "§7Du hast §e" + targetPlayer.getName() + " §7insgesamt §e" + amountFormat.format(amount) + " §7Coins gesetzt!");
                        targetPlayer.sendMessage(CoinManager.prefix + "§7Dir wurden von §e" + player.getName() + " §7insgesamt §e" + amountFormat.format(amount) + " §7Coins gesetzt!");
                    } else {
                        OfflinePlayer offlineTargetPlayer = Bukkit.getServer().getOfflinePlayer(args[0]);
                        if(!offlineTargetPlayer.hasPlayedBefore()) {
                            player.sendMessage(CoinManager.prefix + "§cDieser Spieler konnte nicht gefunden werden.");
                            return false;
                        }
                        String offlineTargetUUID = offlineTargetPlayer.getUniqueId().toString();
                        if(!CoinAPI.playerExists(offlineTargetUUID)) {
                            player.sendMessage(CoinManager.prefix + "§cDieser Spieler konnte nicht gefunden werden.");
                            return false;
                        }
                        CoinManager.sendtoDiscord("Der Spieler **" + player.getName() + "** hat **" + offlineTargetPlayer.getName() + "** insgesamt **" + amountFormat.format(amount) + "** Coins gesetzt", CoinManager.coinsURL);
                        CoinAPI.setCoins(offlineTargetUUID, offlineTargetPlayer.getName(), amount);
                        player.sendMessage(CoinManager.prefix + "§7Du hast §e" + offlineTargetPlayer.getName() + " §7insgesamt §e" + amountFormat.format(amount) + " §7Coins gesetzt!");
                    }
                }
            }
        } else {
            CoinManager.sender.sendMessage(CoinManager.prefix + "§cDiesen Befehl kann nur ein Spieler ausführen!");
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> tabComplete = new ArrayList<String>();
        Player player = (Player) sender;

        if (player.hasPermission("raveq.coinmanager.tabcomplete")) {
            if(args.length == 1) {
                tabComplete.add("SPIELER");

                return tabComplete;
            }
            if (args.length == 2) {
                tabComplete.add("add");
                tabComplete.add("set");
                tabComplete.add("remove");

                return tabComplete;
            }

            if (args.length == 3) {
                tabComplete.add("ANZAHL");

                return tabComplete;
            }
        }

        return null;
    }
}
