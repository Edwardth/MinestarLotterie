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

package de.minestar.MinestarLotterie.utils;

import java.util.TimerTask;

import de.minestar.MinestarLotterie.dataManager.DrawingManager;
import de.minestar.minestarlibrary.utils.ConsoleUtils;

public class LotterieTask extends TimerTask {

    public LotterieTask(DrawingManager drawingManager) {
        this.drawingManager = drawingManager;
    }

    private DrawingManager drawingManager;

    @Override
    public void run() {
        drawingManager.draw(true);
        ConsoleUtils.printInfo("MinestarLotterie", "Automatische Ziehung wird ausgeführt.");
    }
}
