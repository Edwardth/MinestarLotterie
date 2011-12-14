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
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.java.JavaPlugin;

import com.minestar.MinestarLotterie.dataManager.ConnectionManager;
import com.minestar.MinestarLotterie.dataManager.DatabaseManager;
import com.minestar.MinestarLotterie.dataManager.DrawingManager;
import com.minestar.MinestarLotterie.commands.CommandList;
import com.minestar.MinestarLotterie.listeners.PlayerJoinListener;
import com.minestar.MinestarLotterie.utils.LogUnit;
import com.minestar.MinestarLotterie.utils.LotterieTask;

public class Main extends JavaPlugin {

    private static final String PLUGIN_NAME = "MinestarLotterie";
    public static LogUnit log = LogUnit.getInstance(PLUGIN_NAME);
    private DatabaseManager dbManager;
    public static DrawingManager drawingManager;
    public static FileConfiguration config;
    private CommandList commandList;
    public static Server server;
    Timer t;
    LotterieTask task;

    public void onEnable() {
        if (ConnectionManager.initialize()) {
            dbManager = new DatabaseManager(getServer());
            drawingManager = new DrawingManager(dbManager);
            commandList = new CommandList(getServer());
            server = getServer();
            server.getPluginManager().registerEvent(Type.PLAYER_JOIN,
                    new PlayerJoinListener(), Priority.Normal, this);
            loadConfig();
            log.printInfo("enabled");
            if (config.getBoolean("automatically_drawing", true)) {
                t = new Timer();
                task = new LotterieTask();
                t.schedule(task, getNextDrawingTime());
            }
        }
        else {
            log.printWarning("Can't connect to Database!");
        }
    }

    public void onDisable() {
        ConnectionManager.closeConnection();
        log.printInfo("[MiestarLotterie] disabled");
    }

    public void loadConfig() {
        File pluginDir = getDataFolder();
        if (!pluginDir.exists())
            pluginDir.mkdirs();
        File configFile = new File(pluginDir.getAbsolutePath().concat(
                "/config.yml"));
        if (!configFile.exists())
            createConfig();
        else
            config = getConfig();
    }

    public boolean onCommand(CommandSender sender, Command command,
            String label, String[] args) {
        commandList.handleCommand(sender, label, args);
        return true;
    }

    public void createConfig() {
        config = getConfig();
        config.addDefault("drawing_of_lots", 1);
        config.addDefault("range_of_numbers", 9);
        config.addDefault("automatically_drawing", true);
        config.addDefault("weekday_of_drawing", 7);// 1 = Montag, 2 = Dienstag,
                                                   // ... 7 = Sontag
        config.addDefault("time_of_drawin", 20);
        config.addDefault("prize_value", 10);
        config.addDefault("prize_ID", 266);
        config.addDefault("prize_name", "Gold");
        config.addDefault("stake_value", 1);
        config.addDefault("stake_ID", 266);
        config.addDefault("stake_name", "Gold");
        config.options().copyDefaults(true);
        saveConfig();
    }

    public Date getNextDrawingTime() {
        Calendar time = Calendar.getInstance();
        long t = dbManager.loadTime();
        if (t > 0) {
            time.setTimeInMillis(t);
        }
        else {
            time.set(Calendar.DAY_OF_WEEK,
                    config.getInt("weekday_of_drawin", 7) + 1);
            time.set(Calendar.HOUR_OF_DAY, config.getInt("time_of_drawin", 20));
            time.set(Calendar.MINUTE, 0);
            time.set(Calendar.SECOND, 0);
        }
        log.printInfo(String.format("Automatische ziehung um: %s ",
                time.getTime()));
        return time.getTime();
    }
}
