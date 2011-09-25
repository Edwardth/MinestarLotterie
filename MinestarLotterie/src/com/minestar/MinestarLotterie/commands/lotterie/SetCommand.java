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

package com.minestar.MinestarLotterie.commands.lotterie;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import com.minestar.MinestarLotterie.Main;
import com.minestar.MinestarLotterie.commands.Command;

public class SetCommand extends Command {
    public SetCommand(String syntax, String arguments, String node,
            Server server) {
        super(syntax, arguments, node, server);
        this.description = "Zum bieten.";
    }

    public void execute(String[] args, Player player) {
        int drawings = Main.config.getInt("drawing_of_lots", 1);
        int range = Main.config.getInt("range_of_numbers", 9);
        int stakeValue = Main.config.getInt("stake_value", 1);
        int stakeID = Main.config.getInt("stake_ID", 266);
        player.sendMessage("" + player.getItemInHand().getTypeId());
        int[] numbers = new int[drawings];
        for (int i = 0; i < drawings; i++) {
            try {
                numbers[i] = Integer.parseInt(args[i]);
            }
            catch (Exception e) {
                player.sendMessage("Der " + (i + 1)
                        + ". Parameter ist keine Zahl!");
                return;
            }
            if (numbers[i] < 1 || numbers[i] > range) {
                player.sendMessage("Die Zahlen muessen zwischen 1 und " + range
                        + " sein");
                return;
            }
        }
        if (player.getItemInHand().getTypeId() == stakeID
                && player.getItemInHand().getAmount() >= stakeValue) {
            if (player.getItemInHand().getAmount() == stakeValue) {
                player.getInventory().clear(
                        player.getInventory().getHeldItemSlot());
            }
            else {
                player.getItemInHand().setAmount(
                        player.getItemInHand().getAmount() - stakeValue);
            }
        }
        else {
            player.sendMessage(ChatColor.RED + "Du brauchst mindenstens "
                    + stakeValue + " "
                    + Main.config.getString("stake_name", "Gold")
                    + " um ein Tipp abzugeben!");
            return;
        }
        if (!Main.drawingManager.addstake(player, numbers))
            return;

        StringBuilder sb = new StringBuilder("");
        for (int number : numbers) {
            sb.append(number);
            sb.append(", ");
        }
        player.sendMessage("Dein Tipp wurde erfolgreich abgegeben. "
                + sb.substring(0, sb.length() - 2));
        return;
    }
}
