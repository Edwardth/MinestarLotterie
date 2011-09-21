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

package com.minestar.MinestarLotterie.dateManager;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.minestar.MinestarLotterie.Main;

public class DrawingManager {
    private ArrayList<String> currentusers;
    private TreeMap<Integer, ArrayList<String>> currentdrawing;
    private TreeMap<String, Integer> winner;
    private Random random;

    public DrawingManager() {
        currentusers = new ArrayList<String>();
        currentdrawing = new TreeMap<Integer, ArrayList<String>>();
        winner = new TreeMap<String, Integer>();
        random = new Random();
    }

    public Boolean addstake(Player player, int[] numbers) {
        String name = player.getName();
        if (currentusers.contains(name)) {
            player.sendMessage("Du kannst nur einmal pro Ziehung einen Tipp abgeben!");
            return false;
        }
        for (int i = 0; i < numbers.length; i++) {
            if (!currentdrawing.containsKey(numbers[i]))
                currentdrawing.put(numbers[i], new ArrayList<String>());
            currentdrawing.get(numbers[i]).add(name);
        }
        currentusers.add(name);
        return true;
    }

    public Boolean isWinner(Player player) {
        if (winner.containsKey(player.getName()))
            return true;
        return false;
    }

    public void draw() {
        int itemp = random.nextInt(10);
        draw(itemp);
    }

    public void draw(int itemp) {
        ArrayList<String> temp = new ArrayList<String>();
        if (!currentdrawing.containsKey(itemp))
            return;
        temp = currentdrawing.get(itemp);
        if (temp.isEmpty())
            return;
        Player player;
        for (int i = 0; i < temp.size(); i++) {
            // if(winner.containsKey(temp.get(i)))
            winner.put(temp.get(i), Main.config.getInt("prize_value", 10));
            Main.log.info("Es gibt einen Gewinner");
            player = Main.server.getPlayer(temp.get(i));
            if (player != null)
                player.sendMessage(ChatColor.GOLD
                        + "Du hast in der MinestarLotterie gewonnen!");
        }
        currentdrawing.clear();
        currentusers.clear();
    }

    public void get(Player player) {
        String name = player.getName();
        if (isWinner(player)) {
            ItemStack itemstack = new ItemStack(Main.config.getInt("prize_ID",
                    264), winner.get(name));
            player.getInventory().addItem(itemstack);
            player.sendMessage("Hier dein Gewinn!");
            winner.remove(player.getName());
            return;
        }
        player.sendMessage("Du hast leider nichts gewonnen");
    }
}
