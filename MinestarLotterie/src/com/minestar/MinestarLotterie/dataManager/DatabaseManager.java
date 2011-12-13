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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.TreeMap;
//import java.util.ArrayList;

import org.bukkit.Server;

import com.minestar.MinestarLotterie.dataManager.DatabaseManager;
import com.minestar.MinestarLotterie.Main;

public class DatabaseManager {

    private final Connection con = ConnectionManager.getConnection();

    // private final Server server;

    // PreparedStatements for the often used SQLLite Queries.
    private PreparedStatement addStake = null;
    // private PreparedStatement updateStakes = null;
    private PreparedStatement deleteStakes = null;
    private PreparedStatement addWinner = null;
    private PreparedStatement updateWinner = null;
    private PreparedStatement deleteWinner = null;
    private PreparedStatement addDrawing = null;

    /**
     * Uses for all database transactions
     * 
     * @param server
     */
    public DatabaseManager(Server server) {
        // this.server = server;
        try {
            // create tables if not exists and compile the prepare Statements
            initiate();
            /*
             * addStakes = con
             * .prepareStatement("INSERT INTO stakes (id, players) VALUES (?,?);"
             * );
             */
            addStake = con
                    .prepareStatement("INSERT INTO stakes (player, number) VALUES (?,?);");
            /*
             * updateStakes = con
             * .prepareStatement("UPDATE stakes SET players = ? WHERE id = ?;");
             */
            deleteStakes = con.prepareStatement("DELETE FROM stakes;");
            addWinner = con
                    .prepareStatement("INSERT INTO winner (player, value) VALUES (?,?);");
            updateWinner = con
                    .prepareStatement("UPDATE winner SET value = ? WHERE player = ?;");
            deleteWinner = con
                    .prepareStatement("DELETE FROM winner WHERE player = ?;");
            addDrawing = con
                    .prepareStatement("INSERT INTO draws (time, number, auto, winner) VALUES (?,?,?,?);");
        }
        catch (Exception e) {
            Main.log.printError("Error while initiate of DatabaseManager!", e);
        }
    }

    private void initiate() throws Exception {
        // check the database structure
        createTables();
    }

    private void createTables() throws Exception {
        // create the table for storing the stakes.
        con.createStatement().executeUpdate(
                "CREATE TABLE IF NOT EXISTS `stakes` ("
                        + "`player` varchar(32) PRIMARY KEY,"
                        + "`number` INTEGER NOT NULL DEFAULT '0');");
        /*
         * con.createStatement().executeUpdate(
         * "CREATE TABLE IF NOT EXISTS `stakes` (" + "`id` INTEGER PRIMARY KEY,"
         * + "`players` text DEFAULT '');");
         */

        // create the table for storing the winner.
        con.createStatement().executeUpdate(
                "CREATE TABLE IF NOT EXISTS `winner` ("
                        + "`player` varchar(32) PRIMARY KEY,"
                        + "`value` INTEGER NOT NULL DEFAULT '0');");
        // create the table for storing the draws.
        con.createStatement().executeUpdate(
                "CREATE TABLE IF NOT EXISTS `draws` ("
                        + "`id` INTEGER PRIMARY KEY,"
                        + "`time` LONG NOT NULL DEFAULT '0',"
                        + "`number` INTEGER NOT NULL DEFAULT '0',"
                        + "`auto` BOOLEAN DEFAULT TRUE,"
                        + "`winner` text);");
        // create the table for storing the next draw-time.
        con.createStatement().executeUpdate(
                "CREATE TABLE IF NOT EXISTS `nextdraw` ("
                        + "`id` INTEGER PRIMARY KEY,"
                        + "`time` LONG NOT NULL DEFAULT '0',"
                        + "`price` INT NOT NULL DEFAULT '0');");
        con.commit();
    }

    public TreeMap<String, Integer> loadWinnerFromDatabase() {

        TreeMap<String, Integer> winner = new TreeMap<String, Integer>();
        try {
            ResultSet rs = con.createStatement().executeQuery(
                    "SELECT player,value FROM winner");
            while (rs.next()) {

                String player = rs.getString(1);
                int value = rs.getInt(2);
                winner.put(player, value);
            }
        }
        catch (Exception e) {
            Main.log.printError(
                    "Error while loading the winner from database!", e);
        }
        Main.log.printInfo("Loaded sucessfully " + winner.size() + " Winner");
        return winner;
    }

    /*public long loadNextDraw() {
        try {
            ResultSet rs = con.createStatement().executeQuery(
                    "SELCET time FROM nextdraw");
            while (rs.next()) {
                return rs.getLong(2);
            }
        }
        catch (Exception e) {
            Main.log.printError(
                    "Error while loading the nextdraw from database!", e);
        }
        return 0;
    }*/

    public long loadTime() {
        long time = 0;
        try {
            ResultSet rs = con.createStatement().executeQuery(
                    "SELECT time FROM nextdraw WHERE id='1'");
            if (rs.next())
                time = rs.getLong(1);
        }
        catch (Exception e) {
            Main.log.printError("Error while loading the time from database!",
                    e);
        }
        return time;
    }

    /*public StakesAndPlayer loadStakesAndPlayerFromDatabase() {
        TreeMap<Integer, ArrayList<String>> tree = new TreeMap<Integer, ArrayList<String>>();
        ArrayList<String> player = new ArrayList<String>();
        try {
            ResultSet rs = con.createStatement().executeQuery(
                    "SELECT id,players FROM stakes");
            while (rs.next()) {
                int id = rs.getInt(1);
                String value = rs.getString(2);
                ArrayList<String> tmp = new ArrayList<String>();
                for (String stmp : value.split(",")) {
                    tmp.add(stmp);
                    if (!player.contains(stmp))
                        player.add(stmp);
                }
                tree.put(id, tmp);
            }
        }
        catch (Exception e) {
            Main.log.printError(
                    "Error while loading the stakes and Players from database!", e);
        }
        Main.log.printInfo("Loaded sucessfully " + tree.size() + " Stakes");
        Main.log.printInfo("Loaded sucessfully " + player.size() + " Player");
        StakesAndPlayer sap = new StakesAndPlayer(tree, player);
        return sap;
    }
    */

    public boolean addStake(String player, int number) {
        try {
            /*
             * // INSERT INTO stakes (id, players) VALUES (?,?);
             * addStakes.setInt(1, id); addStakes.setString(2, players);
             * addStakes.executeUpdate(); con.commit();
             */
            // INSERT INTO stakes (player, number) VALUES (?,?);
            addStake.setString(1, player);
            addStake.setInt(2, number);
            addStake.executeUpdate();
            con.commit();
        }
        catch (Exception e) {
            Main.log.printError("Error wihile adding a new stake to database!",
                    e);
            return false;
        }
        return true;
    }

    /*
     * public boolean updateStakes(int id, String players) { try { // UPDATE
     * stakes SET players = ? WHERE id = ?; updateStakes.setString(1, players);
     * updateStakes.setInt(2, id); updateStakes.executeUpdate(); con.commit(); }
     * catch (Exception e) {
     * Main.log.printError("Error while updateing stakes!", e); return false; }
     * return true; }
     */

    public boolean deleteStakes() {
        try {
            // DELETE FROM stakes;
            deleteStakes.executeUpdate();
            con.commit();
        }
        catch (Exception e) {
            Main.log.printError("Error while deleting stakes!", e);
            return false;
        }
        return true;
    }

    public boolean addWinner(String player, int value) {
        try {
            // INSERT INTO winner (player, value) VALUES (?,?);
            addWinner.setString(1, player);
            addWinner.setInt(2, value);
            addWinner.executeUpdate();
            con.commit();
        }
        catch (Exception e) {
            Main.log.printError("Error while adding a new winner to database!",
                    e);
            return false;
        }
        return true;
    }

    public boolean updateWinner(String player, int value) {
        try {
            // UPDATE winner SET value = ? WHERE player = ?;
            updateWinner.setInt(1, value);
            updateWinner.setString(2, player);
            updateWinner.executeUpdate();
            con.commit();
        }
        catch (Exception e) {
            Main.log.printError("Error while updateing a winner!", e);
            return false;
        }
        return true;
    }

    public boolean deleteWinner(String player) {
        try {
            // DELETE FROM winner WHERE player = ?;
            deleteWinner.setString(1, player);
            deleteWinner.executeUpdate();
            con.commit();
        }
        catch (Exception e) {
            Main.log.printError("Error while deleting a winner", e);
            return false;
        }
        return true;
    }

    public boolean addDrawing(int number, boolean auto, String winner) {
        try {
            // INSERT INTO draws (time, number, auto, winner) VALUES (?,?,?,?);
            Date date = new Date();
            addDrawing.setLong(1, date.getTime());
            addDrawing.setInt(2, number);
            addDrawing.setBoolean(3, auto);
            addDrawing.setString(4, winner);
            addDrawing.executeUpdate();
            con.commit();
        }
        catch (Exception e) {
            Main.log.printError("Error while adding a draw!", e);
            return false;
        }
        return true;
    }

}
