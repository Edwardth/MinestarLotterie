/*
 * Copyright (C) 2011 MineStar.de 
 * 
 * This file is part of MinestarLotterie.
 * 
 * MinestarLotterie is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * 
 * MinestarLotterie is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with MinestarLotterie.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.minestar.MinestarLotterie.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

import com.minestar.MinestarLotterie.Main;

public class PlayerJoinListener extends PlayerListener {
    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (Main.drawingManager.isWinner(player))
            player.sendMessage(ChatColor.GOLD
                    + "Herzlichen glückwunsch, du hast in der MinestarLotterie gewonnen");
        // Wenn Player gewonnen hat gekommt er eine Message
    }
}
