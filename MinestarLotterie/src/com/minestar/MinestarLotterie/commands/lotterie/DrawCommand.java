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

import org.bukkit.Server;
import org.bukkit.entity.Player;

import com.minestar.MinestarLotterie.Main;
import com.minestar.MinestarLotterie.commands.Command;

public class DrawCommand extends Command {
    public DrawCommand(String syntax, String arguments, String node,
            Server server) {
        super(syntax, arguments, node, server);
        this.description = "Zum bieten.";
    }

    public void execute(String[] args, Player player) {
        if (player.isOp()) {
            // Drawing Befehl ausführen.
            if (args.length == 1)
                Main.drawingManager.draw(Integer.parseInt(args[0]));
            Main.drawingManager.draw();
            player.sendMessage("Manuelle Ziehung war erfolgreich.");
        }
        else {
            player.sendMessage("Du bist nicht berechtigt diesen Befehl auszufuehren!");
        }
    }
}
