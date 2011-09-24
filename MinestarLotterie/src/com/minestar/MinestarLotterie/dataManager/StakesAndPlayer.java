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
import java.util.TreeMap;

public class StakesAndPlayer {

    TreeMap<Integer, ArrayList<String>> stakes = null;
    ArrayList<String> player = null;

    public StakesAndPlayer(TreeMap<Integer, ArrayList<String>> stakes,
            ArrayList<String> player) {
        this.stakes = stakes;
        this.player = player;
    }

    public TreeMap<Integer, ArrayList<String>> getStakes() {
        return stakes;
    }

    public ArrayList<String> getPlayer() {
        return player;
    }
}
