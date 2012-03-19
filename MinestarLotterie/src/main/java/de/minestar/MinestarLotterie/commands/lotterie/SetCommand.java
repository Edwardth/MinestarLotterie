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

package de.minestar.MinestarLotterie.commands.lotterie;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.minestar.MinestarLotterie.dataManager.DrawingManager;
import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class SetCommand extends AbstractCommand {
    public SetCommand(String syntax, String arguments, String node, DrawingManager drawingManager, YamlConfiguration config) {
        super(syntax, arguments, node);
        this.description = "Zum bieten.";
        this.drawingManager = drawingManager;
        this.config = config;
    }

    private DrawingManager drawingManager;
    private YamlConfiguration config;

    public void execute(String[] args, Player player) {
        int range = config.getInt("range_of_numbers", 9);
        int stakeValue = config.getInt("stake_value", 1);
        int stakeID = config.getInt("stake_ID", 266);
        int number;
        try {
            number = Integer.parseInt(args[0]);
        } catch (Exception e) {
            PlayerUtils.sendError(player, "MinestarLotterie", "Das ist keine gültige Zahl!");
            return;
        }
        if (number < 1 || number > range) {
            PlayerUtils.sendInfo(player, "MinestarLotterie", "Die Zahl muess zwischen 1 und " + range + " liegen");
            return;
        }
        if (player.getItemInHand().getTypeId() == stakeID && player.getItemInHand().getAmount() >= stakeValue) {
            if (!drawingManager.addstake(player, number))
                return;
            if (player.getItemInHand().getAmount() == stakeValue) {
                player.getInventory().clear(player.getInventory().getHeldItemSlot());
            } else {
                player.getItemInHand().setAmount(player.getItemInHand().getAmount() - stakeValue);
            }
        } else {
            player.sendMessage(ChatColor.RED + "Du brauchst mindenstens " + stakeValue + " " + /*
                                                                                                * Core
                                                                                                * .
                                                                                                * config
                                                                                                * .
                                                                                                * getString
                                                                                                * (
                                                                                                * "stake_name"
                                                                                                * ,
                                                                                                * "Gold"
                                                                                                * )
                                                                                                */"Gold" + " um ein Tipp abzugeben!");
            return;
        }
        player.sendMessage("Dein Tipp wurde erfolgreich abgegeben. " + number);
        return;
    }
}
