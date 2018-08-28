package me.pokerman99.referralsec.commands;

import me.pokerman99.referralsec.Main;
import me.pokerman99.referralsec.Utils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import java.io.IOException;

public class reloadCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        try {
            Main.getInstance().loader.load();
            Main.getInstance().populateVariables();
            Utils.sendMessage(src, "&aReloaded");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return CommandResult.success();
    }
}
