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

import org.bukkit.entity.Player;

import de.minestar.MinestarLotterie.dataManager.DrawingManager;
import de.minestar.minestarlibrary.commands.AbstractCommand;

public class GetCommand extends AbstractCommand {
    public GetCommand(String syntax, String arguments, String node, DrawingManager drawingManager) {
        super(syntax, arguments, node);
        this.description = "Zum bieten.";
        this.drawingManager = drawingManager;
    }

    private DrawingManager drawingManager;

    public void execute(String[] args, Player player) {
        drawingManager.get(player);
    }
}
