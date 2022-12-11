package com.raveq.coinmanager;

import com.raveq.coinmanager.commands.CoinsCommand;
import com.raveq.coinmanager.listener.JoinListener;
import com.raveq.coinmanager.mysql.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

public final class CoinManager extends JavaPlugin {

    public static String prefix = "§8| §bCoinManager §8» ";
    public static String noperm = prefix + "§cDazu hast Du keine Rechte!";

    public static ConsoleCommandSender sender = Bukkit.getConsoleSender();

    public static MySQL mysql;

    private static File file = new File("plugins/CoinManager", "mysql.yml");
    private static FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);

    //Discord URLS
    public static URL coinsURL;

    @Override
    public void onEnable() {
        sender.sendMessage("§8| §bCoinManager §8» §7Das Plugin wurde §eaktiviert!");
        loadMySQL();
        registerListener();
        registerCommands();

        cfg.addDefault("Host", "51.89.69.169");
        cfg.addDefault("Datenbank", "RaveQ-Minecraft-Coins");
        cfg.addDefault("Benutzer", "Benutzername");
        cfg.addDefault("Passwort", "Passwort");
        cfg.options().copyDefaults(true);

        try {
            cfg.save(file);
            coinsURL = new URL("https://discord.com/api/webhooks/1047248937955889262/bD-DpzWIPfOU8aWK8Vx0s9hQz9_60mKD2sAzq5-Rk6xUY7CBTb-dhwjuRxzBRKocLCWP");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onDisable() {
        sender.sendMessage("§8| §bCoinManager §8» §7Das Plugin wurde §cdeaktiviert!");
    }

    public void loadMySQL() {
        String host = cfg.getString("Host");
        String datenbank = cfg.getString("Datenbank");
        String user = cfg.getString("Benutzer");
        String password = cfg.getString("Passwort");
        mysql = new MySQL(host, datenbank, user, password);
        mysql.update("CREATE TABLE IF NOT EXISTS CoinManager(NAME varchar(64), UUID varchar(64), COINS int)");
    }

    public void registerListener() {
        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
    }

    public void registerCommands() {
        getCommand("coins").setExecutor(new CoinsCommand());
        getCommand("coins").setTabCompleter(new CoinsCommand());
    }

    public static void sendtoDiscord(String content, URL url) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("content", "[RaveLogs-CoinManager] " + content);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.addRequestProperty("Content-Type", "application/json");
            connection.addRequestProperty("User-Agent", "Java-DiscordWebhook");
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            OutputStream stream = connection.getOutputStream();
            stream.write(jsonObject.toJSONString().getBytes());
            stream.flush();
            stream.close();

            connection.getInputStream().close();
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
