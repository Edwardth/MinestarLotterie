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

package com.minestar.MinestarLotterie;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import com.minestar.MinestarLotterie.commands.CommandList;
import com.minestar.MinestarLotterie.dateManager.DrawingManager;
import com.minestar.MinestarLotterie.listeners.PlayerJoinListener;

public class Main extends JavaPlugin {

    public static Logger log = Logger.getLogger("Minecraft");
    public static DrawingManager drawingManager;
    public static Configuration config;
    private CommandList commandList;

    public void onEnable() {
        log.info("[MinestarLotterie] enabled");
        drawingManager = new DrawingManager();
        commandList = new CommandList(getServer());
        getServer().getPluginManager().registerEvent(Type.PLAYER_JOIN,
                new PlayerJoinListener(), Priority.Normal, this);
        loadConfig();
    }

    public void onDisable() {
        log.info("[MiestarLotterie] disabled");
    }

    public void loadConfig() {
        File pluginDir = getDataFolder();
        if (!pluginDir.exists())
            pluginDir.mkdirs();
        File configFile = new File(pluginDir.getAbsolutePath().concat(
                "/config.yml"));
        config = new Configuration(new File(pluginDir.getAbsolutePath().concat(
                "/config.yml")));
        if (!configFile.exists())
            createConfig();
        else
            config.load();
    }

    public boolean onCommand(CommandSender sender, Command command,
            String label, String[] args) {
        commandList.handleCommand(sender, label, args);
        return true;
    }

    public void createConfig() {
        config.setProperty("drawing_of_lots", 1);
        config.setProperty("range_of_numbers", 9);
        config.setProperty("weekday_of_drawing", 7);// 1 = Montag, 2 = Dienstag,
                                                    // ... 7 = Sontag
        config.setProperty("time_of_drawin", 20);
        config.setProperty("prize_value", 10);
        config.setProperty("prize_ID", 264);
        config.setProperty("prize_name", "Diamanten");
        config.setProperty("stake_value", 1);
        config.setProperty("stake_ID", 266);
        config.setProperty("stake_name", "Gold");
        config.save();
    }
}
