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

package de.minestar.MinestarLotterie.dataManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.minestar.MinestarLotterie.dataManager.DatabaseManager;
import de.minestar.minestarlibrary.utils.ConsoleUtils;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class DrawingManager {
    private final DatabaseManager dbManager;

    private Random random;
    private YamlConfiguration config;
    private List<String> winners;

    public DrawingManager(DatabaseManager dbManager, YamlConfiguration config) {
        this.dbManager = dbManager;
        this.config = config;
        random = new Random();
        winners = dbManager.getWinners();
    }

    public boolean addstake(Player player, int number) {
        String name = player.getName();
        if (dbManager.hasPlayerBet(name)) {
            PlayerUtils.sendInfo(player, "MinestarLotterie", "Du kannst nur einmal pro Ziehung einen Tipp abgeben!");
            return false;
        }
        dbManager.updatePrize(config.getInt("stake_value", 1));
        dbManager.addStake(name, number);
        return true;
    }

    public boolean isWinner(String player) {
        return winners.contains(player);
    }

    public void addWinner(String winner, int prize) {
        winners.add(winner);
        dbManager.addWinner(winner, prize);
    }

    public void draw(boolean auto) {
        int itemp = random.nextInt(config.getInt("range_of_numbers", 9));
        draw(itemp, auto);
    }

    public void draw(int itemp, boolean auto) {
        int prize = dbManager.loadPrize();
        int rest = 0;
        Date date = new Date();
        ArrayList<String> newWinner = dbManager.getNewWinner(itemp);
        if (newWinner.isEmpty()) {
            ConsoleUtils.printInfo("MinestarLotterie", "Es gibt keine Gewinner.");
            rest = prize;
            dbManager.addDrawing(date.getTime(), itemp, auto, "NULL");
        } else {
            rest = prize % newWinner.size();
            prize = prize / newWinner.size();
            for (String winner : newWinner) {
                Player player = PlayerUtils.getOnlinePlayer(winner);
                if (player != null)
                    PlayerUtils.sendInfo(player, "MinestarLotterie", "Du hast in der Lotterie gewonnen.");
                if (isWinner(winner)) {
                    dbManager.updateWinner(winner, prize);
                } else {
                    addWinner(winner, prize);
                }
            }
            String arrayname = "array" + date.getTime();
            dbManager.addDrawing(date.getTime(), itemp, auto, arrayname);
            dbManager.setArray(arrayname, newWinner);
        }
        if (auto) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(dbManager.loadTime());
            c.add(Calendar.DAY_OF_YEAR, 1);
            c.set(Calendar.DAY_OF_WEEK, config.getInt("weekday_of_drawing", 7) + 1);
            dbManager.setTime(c.getTimeInMillis());
        }
        dbManager.deleteStakes();
        dbManager.setPrize(rest);
    }

    public void get(Player player) {
        String name = player.getName();
        if (isWinner(name)) {
            int prize = dbManager.getPrizeForWinner(name);
            if (dbManager.deleteWinner(name)) {
                ItemStack itemstack = new ItemStack(config.getInt("stake_ID", 266), prize);
                player.getInventory().addItem(itemstack);
                PlayerUtils.sendInfo(player, "MinestarLotterie", "Hier dein Gewinn!");
                return;
            }
            PlayerUtils.sendError(player, "MinestarLotterie", "Fehler bei deleteWinner bitte wende dich an einen Admin.");
        }
        PlayerUtils.sendInfo(player, "MinestarLotterie", "Du hast leider nichts gewonnen.");
    }
}
