/*
 * Copyright (C) 2012 MineStar.de 
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

package de.minestar.MinestarLotterie.utils;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.bukkit.configuration.file.YamlConfiguration;

import de.minestar.MinestarLotterie.dataManager.DatabaseManager;
import de.minestar.minestarlibrary.utils.ConsoleUtils;

public class Utils {
    static public Date getNextDrawingTime(DatabaseManager dbManager, YamlConfiguration config) {
        Calendar time = Calendar.getInstance();
        long t = dbManager.loadTime();
        if (t > 0) {
            time.setTimeInMillis(t);
        } else {
            time.set(Calendar.DAY_OF_WEEK, config.getInt("weekday_of_drawing", 7) + 1);
            time.set(Calendar.HOUR_OF_DAY, config.getInt("time_of_drawin", 20));
            time.set(Calendar.MINUTE, 0);
            time.set(Calendar.SECOND, 0);
            dbManager.setTime(time.getTimeInMillis());
        }
        ConsoleUtils.printInfo("MinestarLotterie", String.format("Automatische ziehung um: %s ", time.getTime()));
        return time.getTime();
    }

    static public YamlConfiguration checkConfig(File dataFolder) {
        YamlConfiguration config = null;
        try {
            File configFile = new File(dataFolder, "config.yml");
            config = new YamlConfiguration();
            if (!configFile.exists()) {
                configFile.createNewFile();
                ConsoleUtils.printWarning("MinestarLotterie", "Can't find config.yml. Plugin creates a default configuration and uses the default values.");
                config.load(configFile);
                config.set("range_of_numbers", 9);
                config.set("autodrawing", true);
                config.set("weekday_of_drawing", 7);// 1 = Montag, 2 = Dienstag,
                                                    // ... 7 = Sontag
                config.set("time_of_drawin", 20);
                config.set("stake_value", 1);
                config.save(configFile);
            }
            config.load(configFile);
        } catch (Exception e) {
            ConsoleUtils.printException(e, "MinestarLotterie", "Can't load configuration file!");
        }
        return config;
    }
}
