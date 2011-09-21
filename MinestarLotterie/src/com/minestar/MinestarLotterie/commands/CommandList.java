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

package com.minestar.MinestarLotterie.commands;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.minestar.MinestarLotterie.commands.Command;
import com.minestar.MinestarLotterie.commands.lotterie.DrawCommand;
import com.minestar.MinestarLotterie.commands.lotterie.GetCommand;
import com.minestar.MinestarLotterie.commands.lotterie.SetCommand;

public class CommandList {

    // The commands are stored in this list. The key indicates the
    // commandssyntax and the argument counter
    private HashMap<String, Command> commandList;

    /**
     * Creates an array where the commands are stored in and add them all to the
     * HashMap
     * 
     * @param server
     */
    public CommandList(Server server) {

        // Add an command to this list to register it in the plugin
        Command[] commands = new Command[] {
                new SetCommand("set", "<Zahl>", "set", server),
                new GetCommand("get", "", "get", server),
                new DrawCommand("draw", "<Nummer>", "draw", server) };

        // store the commands in the hash map
        initCommandList(commands);
    }

    public void handleCommand(CommandSender sender, String label, String[] args) {

        if (!(sender instanceof Player))
            return;

        Player player = (Player) sender;

        if (!label.startsWith("/"))
            label = "/" + label;

        // looking for
        Command cmd = commandList.get(label + "_" + args.length);
        if (cmd != null)
            cmd.run(args, player);
        else {
            cmd = commandList.get(label);
            if (cmd != null)
                cmd.run(args, player);
            else {
                player.sendMessage(ChatColor.RED + "Falscher Syntax");

                // FIND RELATED COMMANDS
                LinkedList<Command> cmdList = new LinkedList<Command>();
                for (Entry<String, Command> entry : commandList.entrySet()) {
                    if (entry.getKey().startsWith(label))
                        cmdList.add(entry.getValue());
                }

                // PRINT SYNTAX
                while (!cmdList.isEmpty()) {
                    cmd = cmdList.removeFirst();
                    player.sendMessage(ChatColor.GRAY + cmd.getSyntax() + " "
                            + cmd.getArguments());
                }
            }
        }
    }

    /**
     * Stores the commands from the array to a HashMap. The key is generated by
     * the followning: <br>
     * <code>syntax_numberOfArguments</code> <br>
     * Example: /warp create_1 (because create has one argument)
     * 
     * @param cmds
     *            The array list for commands
     */
    private void initCommandList(Command[] cmds) {

        commandList = new HashMap<String, Command>(cmds.length, 1.0f);
        for (Command cmd : cmds) {
            String key = "";
            // when the command has a variable count of arguments or
            // when the command has a function and sub commands
            // a normal command(no subcommands/fix argument count)
            key = cmd.getSyntax() + "_"
                    + (cmd.getArguments().split("<").length - 1);

            commandList.put(key, cmd);
        }
    }
}
