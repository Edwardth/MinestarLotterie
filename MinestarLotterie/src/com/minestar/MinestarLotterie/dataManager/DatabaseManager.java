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
import java.util.ArrayList;

import com.minestar.MinestarLotterie.dataManager.DatabaseManager;
import com.minestar.MinestarLotterie.Main;

public class DatabaseManager {

    private final Connection con = ConnectionManager.getConnection();

    // PreparedStatements for the often used SQLLite Queries.
    private PreparedStatement addDrawing = null;
    private PreparedStatement addStake = null;
    private PreparedStatement addWinner = null;
    private PreparedStatement deleteWinner = null;
    private PreparedStatement getNewWinner = null;
    private PreparedStatement getPrizeForWinner = null;
    private PreparedStatement hasPlayerBet = null;
    private PreparedStatement isWinner = null;
    private PreparedStatement setPrize = null;
    private PreparedStatement setTime = null;
    private PreparedStatement updatePrize = null;
    private PreparedStatement updateWinner = null;
    private PreparedStatement getArray = null;
    private PreparedStatement setArray = null;

    /**
     * Uses for all database transactions
     * 
     * @param server
     */
    public DatabaseManager() {
        try {
            // create tables if not exists and compile the prepare Statements
            initiate();
            addStake = con
                    .prepareStatement("INSERT INTO stakes (player, number) VALUES (?,?);");
            addWinner = con
                    .prepareStatement("INSERT INTO winner (player, value) VALUES (?,?);");
            updateWinner = con
                    .prepareStatement("UPDATE winner SET value = value + ? WHERE player = ?;");
            deleteWinner = con
                    .prepareStatement("DELETE FROM winner WHERE player = ?;");
            addDrawing = con
                    .prepareStatement("INSERT INTO draws (time, number, auto, array) VALUES (?,?,?,?);");
            updatePrize = con
                    .prepareStatement("UPDATE nextdraw SET prize = prize + ? WHERE id = 1;");
            setPrize = con
                    .prepareStatement("UPDATE nextdraw SET prize = ? WHERE id = 1;");
            setTime = con
                    .prepareStatement("UPDATE nextdraw SET time = ? WHERE id = 1;");
            getNewWinner = con
                    .prepareStatement("SELECT player FROM stakes WHERE number = ?;");
            getPrizeForWinner = con
                    .prepareStatement("SELECT value FROM winner WHERE player = ?;");
            hasPlayerBet = con
                    .prepareStatement("SELECT player FROM stakes WHERE player = ?;");
            isWinner = con
                    .prepareStatement("SELECT player FROM winner WHERE player = ?;");
            getArray = con
                    .prepareStatement("SELECT playername FROM winnerarrays WHERE arrayname = ?;");
            setArray = con
                    .prepareStatement("INSERT INTO winnerarrays (arrayname, playername) VALUES (?,?);");
        }
        catch (Exception e) {
            Main.log.printError("Error while initiate of DatabaseManager!", e);
        }
    }

    private void initiate() throws Exception {
        // check the database structure
        createTables();
        createNextDraw();
    }

    private void createTables() throws Exception {
        // create the table for storing the bets.
        con.createStatement().executeUpdate(
                "CREATE TABLE IF NOT EXISTS `stakes` ("
                        + "`player` varchar(32) PRIMARY KEY,"
                        + "`number` INTEGER NOT NULL DEFAULT '0');");

        // create the table for storing the winner.
        con.createStatement().executeUpdate(
                "CREATE TABLE IF NOT EXISTS `winner` ("
                        + "`player` varchar(32) PRIMARY KEY,"
                        + "`value` INTEGER NOT NULL DEFAULT '0');");
        // create the table for storing the draws.
        con.createStatement().executeUpdate(
                "CREATE TABLE IF NOT EXISTS `draws` ("
                        + "`time` LONG PRIMARY KEY,"
                        + "`number` INTEGER NOT NULL DEFAULT '0',"
                        + "`auto` BOOLEAN DEFAULT TRUE,"
                        + "`array` varchar(32) NOT NULL);");
        // create the table for storing the next draw-time and the prize.
        con.createStatement().executeUpdate(
                "CREATE TABLE IF NOT EXISTS `nextdraw` ("
                        + "`id` INTEGER PRIMARY KEY,"
                        + "`time` LONG NOT NULL DEFAULT '0',"
                        + "`prize` INT NOT NULL DEFAULT '0');");
        // create the table for the arrays.
        con.createStatement().executeUpdate(
                "CREATE TABLE IF NOT EXISTS `winnerarrays` ("
                        + "`arrayname` varchar(32) NOT NULL,"
                        + "`playername` varchar(32) NOT NULL);");
        con.commit();
    }

    private void createNextDraw() throws Exception {
        ResultSet rs = con.createStatement().executeQuery(
                "SELECT id FROM nextdraw WHERE 1");
        if (!rs.next()) {
            con.createStatement().executeUpdate(
                    "INSERT INTO nextdraw (id, time, prize) VALUES (1,0,0);");
            con.commit();
        }
    }

    public void setTime(long time) {
        try {
            setTime.setLong(1, time);
            setTime.executeUpdate();
            con.commit();
        }
        catch (Exception e) {
            Main.log.printError("Error while set the time in the database!", e);
        }
    }

    public long loadTime() {
        long time = 0;
        try {
            ResultSet rs = con.createStatement().executeQuery(
                    "SELECT time FROM nextdraw WHERE id = 1;");
            if (rs.next())
                time = rs.getLong(1);
        }
        catch (Exception e) {
            Main.log.printError("Error while loading the time from database!",
                    e);
        }
        return time;
    }

    public int loadPrize() {
        int prize = 0;
        try {
            ResultSet rs = con.createStatement().executeQuery(
                    "SELECT prize FROM nextdraw WHERE id = 1;");
            if (rs.next())
                prize = rs.getInt(1);
        }
        catch (Exception e) {
            Main.log.printError("Error while loading the time from database!",
                    e);
        }
        return prize;
    }

    public void updatePrize(int prize) {
        try {
            updatePrize.setInt(1, prize);
            updatePrize.executeUpdate();
            con.commit();
        }
        catch (Exception e) {
            Main.log.printError(
                    "Error while updateing the prize in the database!", e);
        }
    }

    public void setPrize(int prize) {
        try {
            setPrize.setInt(1, prize);
            setPrize.executeUpdate();
            con.commit();
        }
        catch (Exception e) {
            Main.log.printError("Error while set the prize in the database!", e);
        }
    }

    public ArrayList<String> getNewWinner(int number) {
        ArrayList<String> winner = new ArrayList<String>();
        try {
            getNewWinner.setInt(1, number);
            ResultSet rs = getNewWinner.executeQuery();
            con.commit();
            while (rs.next()) {
                winner.add(rs.getString(1));
            }
        }
        catch (Exception e) {
            Main.log.printError(
                    "Error while loading the new winner from database!", e);
        }
        return winner;
    }

    public boolean hasPlayerBet(String player) {
        try {
            hasPlayerBet.setString(1, player);
            ResultSet rs = hasPlayerBet.executeQuery();
            con.commit();
            if (rs.next())
                return true;
        }
        catch (Exception e) {
            Main.log.printError("Error while look if a Player has bet!", e);
            return true;
        }
        return false;
    }

    public boolean addStake(String player, int number) {
        try {
            // INSERT INTO stakes (player, number) VALUES (?,?);
            addStake.setString(1, player);
            addStake.setInt(2, number);
            addStake.executeUpdate();
            con.commit();
        }
        catch (Exception e) {
            Main.log.printError("Error while adding a new stake to database!",
                    e);
            return false;
        }
        return true;
    }

    public boolean deleteStakes() {
        try {
            con.createStatement().executeUpdate("DELETE FROM stakes;");
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

    public boolean isWinner(String player) {
        try {
            isWinner.setString(1, player);
            ResultSet rs = isWinner.executeQuery();
            con.commit();
            if (rs.next())
                return true;
        }
        catch (Exception e) {
            Main.log.printError("Error while look if a player is a Winner", e);
        }
        return false;
    }

    public int getPrizeForWinner(String player) {
        int prize = 0;
        try {
            getPrizeForWinner.setString(1, player);
            ResultSet rs = getPrizeForWinner.executeQuery();
            con.commit();
            if (rs.next())
                prize = rs.getInt(1);
        }
        catch (Exception e) {
            Main.log.printError("Error while get the prize for a winner!", e);
        }
        return prize;
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

    public boolean addDrawing(long time, int number, boolean auto, String array) {
        try {
            // INSERT INTO draws (time, number, auto, array) VALUES (?,?,?,?);
            addDrawing.setLong(1, time);
            addDrawing.setInt(2, number);
            addDrawing.setBoolean(3, auto);
            addDrawing.setString(4, array);
            addDrawing.executeUpdate();
            con.commit();
        }
        catch (Exception e) {
            Main.log.printError("Error while adding a draw!", e);
            return false;
        }
        return true;
    }
    
    public ArrayList<String> getArray(String arrayname)
    {
        ArrayList<String> winner = new ArrayList<String>();
        try {
            getArray.setString(1, arrayname);
            ResultSet rs = getArray.executeQuery();
            con.commit();
            while(rs.next())
            {
                winner.add(rs.getString(1));
            }
        }
        catch (Exception e)
        {
            Main.log.printError("Error while getting a winnerarray", e);
            return null;
        }
        return winner;
    }
    
    public boolean setArray(String arrayname, ArrayList<String> playernames)
    {
        try {
            for(String playername : playernames)
            {
                setArray.setString(1, arrayname);
                setArray.setString(2, playername);
                setArray.executeUpdate();
            }
            con.commit();
        }
        catch (Exception e)
        {
            Main.log.printError("Error while adding a winnerarray", e);
            return false;
        }
        return true;
    }

}
