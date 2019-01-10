package me.pokerman99.referralsec.commands;

import com.google.common.reflect.TypeToken;
import me.pokerman99.referralsec.Main;
import me.pokerman99.referralsec.Utils;
import me.pokerman99.referralsec.rankUpperParser;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import java.util.List;

import static me.pokerman99.referralsec.Main.rootNode;

public class checkCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {


        if (rootNode.getNode("referrals", src.getIdentifier()).isVirtual()) {
            Utils.sendMessage(src, Main.checkCommandNoReferees);
            return CommandResult.success();
        }

        try {
            //Getting the list of people they've referred
            List<String> referred = rootNode.getNode("referrals", src.getIdentifier()).getList(TypeToken.of(String.class));

            {
                //If they HAVE referred someone but currently are not
                if (referred.isEmpty()) {
                    Utils.sendMessage(src, Main.checkCommandNoReferees);
                    return CommandResult.success();
                }

            }



            //Make the message that it sent to the player
            StringBuilder message = new StringBuilder(Main.checkCommandStringBuilderHeader);
            rankUpperParser rankUpperParser = Main.rankUpperParser;
            {
                referred.forEach(s -> {
                    try {

                        message.append("&a" + Utils.getUsername(s) + " - " + rankUpperParser.getPlayTime(s) + " minutes\n");
                    } catch (Exception e) {
                        //Empty
                    }
                });

            }

            Utils.sendMessage(src, message.toString());

        } catch (ObjectMappingException e) { //This should never happen
            Utils.sendMessage(src, "&cThere is an issue with your player storage, message a staff member\n");
            e.printStackTrace();
        }

        return CommandResult.success();
    }
}
