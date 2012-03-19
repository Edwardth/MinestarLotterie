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

package de.minestar.MinestarLotterie;

import java.util.Timer;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

import de.minestar.MinestarLotterie.commands.lotterie.DrawCommand;
import de.minestar.MinestarLotterie.commands.lotterie.GetCommand;
import de.minestar.MinestarLotterie.commands.lotterie.LotterieCommand;
import de.minestar.MinestarLotterie.commands.lotterie.SetCommand;
import de.minestar.MinestarLotterie.dataManager.DatabaseManager;
import de.minestar.MinestarLotterie.dataManager.DrawingManager;
import de.minestar.MinestarLotterie.listeners.PlayerJoinListener;
import de.minestar.MinestarLotterie.utils.LotterieTask;
import de.minestar.MinestarLotterie.utils.Utils;
import de.minestar.minestarlibrary.AbstractCore;
import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.commands.CommandList;

public class Core extends AbstractCore {

    private DatabaseManager dbManager;
    public DrawingManager drawingManager;
    private PlayerJoinListener playerJoinListener;
    LotterieTask lotterieTask;
    public YamlConfiguration config;
    Timer timer;

    public Core() {
        this("MinestarLotterie");
    }

    public Core(String name) {
        super(name);
        config = Utils.checkConfig(getDataFolder());
        timer = new Timer();
    }

    @Override
    protected boolean createCommands() {
        //@formatter:off
        AbstractCommand[] commands = new AbstractCommand[] {
                new LotterieCommand("/lotterie","","minestarlotterie.set",
                        new SetCommand("set", "<Zahl>", "minestarlotterie.set",drawingManager,config),
                        new GetCommand("get", "", "minestarlotterie.get",drawingManager),
                        new DrawCommand("draw", "<Nummer>", "minestarlotterie.draw",drawingManager),
                        new DrawCommand("draw", "", "minestarlotterie.draw",drawingManager))
        };
        //@formatter:on
        this.cmdList = new CommandList("MinestarLotterie", commands);
        return true;
    }

    @Override
    protected boolean createManager() {
        this.dbManager = new DatabaseManager("MinestarLotterie", this.getDataFolder());
        this.drawingManager = new DrawingManager(this.dbManager, this.config);
        return true;
    }

    @Override
    protected boolean createListener() {
        this.playerJoinListener = new PlayerJoinListener(drawingManager);
        return true;
    }

    @Override
    protected boolean registerEvents(PluginManager pm) {
        Bukkit.getPluginManager().registerEvents(this.playerJoinListener, this);
        return true;
    }

    @Override
    protected boolean createThreads() {
        this.lotterieTask = new LotterieTask(drawingManager);
        return true;
    }

    @Override
    protected boolean startThreads(BukkitScheduler scheduler) {
        if (config.getBoolean("autodrawing", true)) {
            timer.schedule(lotterieTask, Utils.getNextDrawingTime(dbManager, config));
        }
        return true;
    }

}
