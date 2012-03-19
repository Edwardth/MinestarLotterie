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

package de.minestar.MinestarLotterie.dataManager;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.bukkit.configuration.file.YamlConfiguration;

import de.minestar.MinestarLotterie.dataManager.DatabaseManager;
import de.minestar.minestarlibrary.database.AbstractDatabaseHandler;
import de.minestar.minestarlibrary.database.DatabaseConnection;
import de.minestar.minestarlibrary.database.DatabaseType;
import de.minestar.minestarlibrary.database.DatabaseUtils;
import de.minestar.minestarlibrary.utils.ConsoleUtils;

public class DatabaseManager extends AbstractDatabaseHandler {

    // PreparedStatements for the often used SQLLite Queries.
    private PreparedStatement addDrawing;
    private PreparedStatement addStake;
    private PreparedStatement addWinner;
    private PreparedStatement deleteWinner;
    private PreparedStatement getNewWinner;
    private PreparedStatement getPrizeForWinner;
    private PreparedStatement hasPlayerBet;
    private PreparedStatement setPrize;
    private PreparedStatement setTime;
    private PreparedStatement updatePrize;
    private PreparedStatement updateWinner;
    private PreparedStatement getArray;
    private PreparedStatement setArray;

    /**
     * Uses for all database transactions
     * 
     * @param server
     */
    public DatabaseManager(String pluginName, File dataFolder) {
        super(pluginName, dataFolder);
    }

    public void createNextDraw() throws Exception {
        Connection con = dbConnection.getConnection();
        ResultSet rs = con.createStatement().executeQuery("SELECT id FROM nextdraw WHERE 1");
        if (!rs.next()) {
            con.createStatement().executeUpdate("INSERT INTO nextdraw (id, time, prize) VALUES (1,0,0);");
        }
    }

    public void setTime(long time) {
        try {
            // UPDATE nextdraw SET time = ? WHERE id = 1;
            setTime.setLong(1, time);
            setTime.executeUpdate();
        } catch (Exception e) {
            ConsoleUtils.printException(e, "MinestarLotterie", "Error while set the time in the database!");
        }
    }

    public long loadTime() {
        long time = 0;
        try {
            ResultSet rs = dbConnection.getConnection().createStatement().executeQuery("SELECT time FROM nextdraw WHERE id = 1;");
            if (rs.next())
                time = rs.getLong(1);
        } catch (Exception e) {
            ConsoleUtils.printException(e, "MinestarLotterie", "Error while loading the time from database!");
        }
        return time;
    }

    public int loadPrize() {
        int prize = 0;
        try {
            ResultSet rs = dbConnection.getConnection().createStatement().executeQuery("SELECT prize FROM nextdraw WHERE id = 1;");
            if (rs.next())
                prize = rs.getInt(1);
        } catch (Exception e) {
            ConsoleUtils.printException(e, "MinestarLotterie", "Error while loading the Prize from database!");
        }
        return prize;
    }

    public void updatePrize(int prize) {
        try {
            updatePrize.setInt(1, prize);
            updatePrize.executeUpdate();
        } catch (Exception e) {
            ConsoleUtils.printException(e, "MinestarLotterie", "Error while updateing the prize in the database!");
        }
    }

    public void setPrize(int prize) {
        try {
            setPrize.setInt(1, prize);
            setPrize.executeUpdate();
        } catch (Exception e) {
            ConsoleUtils.printException(e, "MinestarLotterie", "Error while set the prize in the database!");
        }
    }

    public ArrayList<String> getNewWinner(int number) {
        ArrayList<String> winner = new ArrayList<String>();
        try {
            getNewWinner.setInt(1, number);
            ResultSet rs = getNewWinner.executeQuery();
            while (rs.next()) {
                winner.add(rs.getString(1));
            }
        } catch (Exception e) {
            ConsoleUtils.printException(e, "MinestarLotterie", "Error while loading the new winner from database!");
        }
        return winner;
    }

    public boolean hasPlayerBet(String player) {
        try {
            hasPlayerBet.setString(1, player);
            ResultSet rs = hasPlayerBet.executeQuery();
            if (rs.next())
                return true;
        } catch (Exception e) {
            ConsoleUtils.printException(e, "Error while look if a Player has bet!");
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
        } catch (Exception e) {
            ConsoleUtils.printException(e, "Error while adding a new stake to database!");
            return false;
        }
        return true;
    }

    public boolean deleteStakes() {
        Connection con = dbConnection.getConnection();
        try {
            con.createStatement().executeUpdate("DELETE FROM stakes;");
        } catch (Exception e) {
            ConsoleUtils.printException(e, "Error while deleting stakes!");
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
        } catch (Exception e) {
            ConsoleUtils.printException(e, "Error while adding a new winner to database!");
            return false;
        }
        return true;
    }

    public ArrayList<String> getWinners() {
        ArrayList<String> winners = new ArrayList<String>();
        try {
            ResultSet rs = dbConnection.getConnection().createStatement().executeQuery("SELECT player FROM winner;");
            while (rs.next())
                winners.add(rs.getString("player"));
        } catch (Exception e) {
            ConsoleUtils.printException(e, "Error while load Winners form the Database!");
        }
        return winners;
    }

    public int getPrizeForWinner(String player) {
        int prize = 0;
        try {
            getPrizeForWinner.setString(1, player);
            ResultSet rs = getPrizeForWinner.executeQuery();
            if (rs.next())
                prize = rs.getInt(1);
        } catch (Exception e) {
            ConsoleUtils.printException(e, "Error while get the prize for a winner!");
        }
        return prize;
    }

    public boolean updateWinner(String player, int value) {
        try {
            // UPDATE winner SET value = ? WHERE player = ?;
            updateWinner.setInt(1, value);
            updateWinner.setString(2, player);
            updateWinner.executeUpdate();
        } catch (Exception e) {
            ConsoleUtils.printException(e, "Error while updateing a winner!");
            return false;
        }
        return true;
    }

    public boolean deleteWinner(String player) {
        try {
            // DELETE FROM winner WHERE player = ?;
            deleteWinner.setString(1, player);
            deleteWinner.executeUpdate();
        } catch (Exception e) {
            ConsoleUtils.printException(e, "Error while deleting a winner");
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
        } catch (Exception e) {
            ConsoleUtils.printException(e, "Error while adding a draw!");
            return false;
        }
        return true;
    }

    public ArrayList<String> getArray(String arrayname) {
        ArrayList<String> winner = new ArrayList<String>();
        try {
            getArray.setString(1, arrayname);
            ResultSet rs = getArray.executeQuery();
            while (rs.next()) {
                winner.add(rs.getString(1));
            }
        } catch (Exception e) {
            ConsoleUtils.printException(e, "Error while getting a winnerarray!");
            return null;
        }
        return winner;
    }

    public boolean setArray(String arrayname, ArrayList<String> playernames) {
        try {
            for (String playername : playernames) {
                setArray.setString(1, arrayname);
                setArray.setString(2, playername);
                setArray.executeUpdate();
            }
        } catch (Exception e) {
            ConsoleUtils.printException(e, "Error while adding a winnerarray!");
            return false;
        }
        return true;
    }

    @Override
    protected DatabaseConnection createConnection(String pluginName, File dataFolder) throws Exception {
        File configFile = new File(dataFolder, "sqlconfig.yml");
        if (configFile.exists()) {
            YamlConfiguration config = new YamlConfiguration();
            config.load(configFile);
            return new DatabaseConnection(pluginName, DatabaseType.SQLLite, config);
        } else {
            DatabaseUtils.createDatabaseConfig(DatabaseType.SQLLite, configFile, pluginName);
            return null;
        }
    }

    @Override
    protected void createStatements(String pluginName, Connection con) throws Exception {
        this.addStake = con.prepareStatement("INSERT INTO stakes (player, number) VALUES (?,?);");
        this.addWinner = con.prepareStatement("INSERT INTO winner (player, value) VALUES (?,?);");
        this.updateWinner = con.prepareStatement("UPDATE winner SET value = value + ? WHERE player = ?;");
        this.deleteWinner = con.prepareStatement("DELETE FROM winner WHERE player = ?;");
        this.addDrawing = con.prepareStatement("INSERT INTO draws (time, number, auto, array) VALUES (?,?,?,?);");
        this.updatePrize = con.prepareStatement("UPDATE nextdraw SET prize = prize + ? WHERE id = 1;");
        this.setPrize = con.prepareStatement("UPDATE nextdraw SET prize = ? WHERE id = 1;");
        this.setTime = con.prepareStatement("UPDATE nextdraw SET time = ? WHERE id = 1;");
        this.getNewWinner = con.prepareStatement("SELECT player FROM stakes WHERE number = ?;");
        this.getPrizeForWinner = con.prepareStatement("SELECT value FROM winner WHERE player = ?;");
        this.hasPlayerBet = con.prepareStatement("SELECT player FROM stakes WHERE player = ?;");
        this.getArray = con.prepareStatement("SELECT playername FROM winnerarrays WHERE arrayname = ?;");
        this.setArray = con.prepareStatement("INSERT INTO winnerarrays (arrayname, playername) VALUES (?,?);");
    }

    @Override
    protected void createStructure(String pluginName, Connection con) throws Exception {
        DatabaseUtils.createStructure(getClass().getResourceAsStream("/structure.sql"), con, pluginName);
        createNextDraw();
    }

}
