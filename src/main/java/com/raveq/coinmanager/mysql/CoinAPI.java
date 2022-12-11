package com.raveq.coinmanager.mysql;

import com.raveq.coinmanager.CoinManager;

import javax.xml.transform.Result;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CoinAPI {

    public static boolean playerExists(String uuid) {
        try {
            ResultSet rs = CoinManager.mysql.query("SELECT * FROM CoinManager WHERE UUID= '" + uuid + "'");

            if (rs != null) {
                if (rs.next())
                    return (rs.getString("UUID") != null);
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void createPlayer(String uuid, String name) {
        if (!playerExists(uuid)) {
            CoinManager.mysql.update("INSERT INTO CoinManager(NAME, UUID, COINS) VALUES ('" + name + "', '" + uuid + "', '0');");
            CoinManager.sender.sendMessage("§8| §bCoinManager §8» §7Der Spieler §e" + name + " §7wurde erfolgreich erstellt!");
        }
    }

    public static void setCoins(String uuid, String name, Integer anzahl) {
        if (playerExists(uuid)) {
            CoinManager.mysql.update("UPDATE CoinManager SET COINS= '" + anzahl + "' WHERE UUID= '" + uuid + "';");
        } else {
            createPlayer(uuid, name);
            setCoins(uuid, name, anzahl);
        }
    }

    public static void addCoins(String uuid, String name, Integer anzahl) {
        if(playerExists(uuid)) {
            setCoins(uuid, name, getCoins(uuid, name) + anzahl);
        } else {
            createPlayer(uuid, name);
            addCoins(uuid, name, anzahl);
        }
    }

    public static void removeCoins(String uuid, String name, Integer anzahl) {
        if(playerExists(uuid)) {
            setCoins(uuid, name, getCoins(uuid, name) - anzahl);
        } else {
            createPlayer(uuid, name);
            removeCoins(uuid, name, anzahl);
        }
    }

    public static Integer getCoins(String uuid, String name) {
        int i = 0;
        if (playerExists(uuid)) {
            try {
                ResultSet rs = CoinManager.mysql.query("SELECT * FROM CoinManager WHERE UUID= '" + uuid + "'");
                if (rs.next()) {
                    i = rs.getInt("COINS");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return i;
    }

}
