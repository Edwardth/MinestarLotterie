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

package com.minestar.MinestarLotterie.dataManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.minestar.MinestarLotterie.dataManager.DatabaseManager;
import com.minestar.MinestarLotterie.Main;

public class DrawingManager {
    private final DatabaseManager dbManager;

    private Random random;

    public DrawingManager(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        random = new Random();
    }

    public boolean addstake(Player player, int number) {
        String name = player.getName();
        if (dbManager.hasPlayerBet(name)) {
            player.sendMessage("Du kannst nur einmal pro Ziehung einen Tipp abgeben!");
            return false;
        }
        dbManager.updatePrize(Main.config.getInt("stake_value",1));
        dbManager.addStake(name, number);
        return true;
    }

    public boolean isWinner(Player player) {
        return dbManager.isWinner(player.getName());
    }

    public void draw(boolean auto) {
        int itemp = random.nextInt(Main.config.getInt("range_of_numbers", 9));
        draw(itemp, auto);
    }

    public void draw(int itemp, boolean auto) {
        int prize = dbManager.loadPrize();
        int rest = 0;
        ArrayList<String> newWinner = dbManager.getNewWinner(itemp);
        if (newWinner.isEmpty()) {
            Main.log.printInfo("Es gibt keinen gewinner");
            rest = prize;
        }
        else {
            rest = prize % newWinner.size();
            prize = prize / newWinner.size();
            for (String winner : newWinner) {
                if (dbManager.isWinner(winner)) {
                    dbManager.updateWinner(winner, prize);
                }
                else {
                    dbManager.addWinner(winner, prize);
                }
            }
        }
        if(auto)
        {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(dbManager.loadTime());
            c.add(Calendar.DAY_OF_YEAR, 1);
            c.set(Calendar.DAY_OF_WEEK, Main.config.getInt("weekday_of_drawing",7)+1);
            dbManager.setTime(c.getTimeInMillis());
        }
        dbManager.deleteStakes();
        dbManager.setPrize(rest);
    }

    public void get(Player player) {
        String name = player.getName();
        if (dbManager.isWinner(name)) {
            int prize = dbManager.getPrizeForWinner(name);
            if (dbManager.deleteWinner(name)) {
                ItemStack itemstack = new ItemStack(Main.config.getInt(
                        "prize_ID", 264), prize);
                player.getInventory().addItem(itemstack);
                player.sendMessage("Hier dein Gewinn!");
                return;
            }
            player.sendMessage(ChatColor.RED
                    + "Fehler bei deleteWinner bitte wende dich an einen Admin");
        }
        player.sendMessage("Du hast leider nichts gewonnen");
    }
}
