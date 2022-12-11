package com.raveq.coinmanager.listener;

import com.raveq.coinmanager.CoinManager;
import com.raveq.coinmanager.mysql.CoinAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = (Player)event.getPlayer();
        CoinAPI.createPlayer(player.getUniqueId().toString(), player.getName());
    }


}
