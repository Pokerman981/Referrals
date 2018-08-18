package me.pokerman99.referralsec.commands;

import com.google.common.reflect.TypeToken;
import me.pokerman99.referralsec.Utils;
import me.pokerman99.referralsec.rankUpperParser;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static me.pokerman99.referralsec.Main.rootNode;

public class claimCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        List<String> referred = null;

        {
            try {
                //Getting the list of people they've referred
                referred = rootNode.getNode("referrals", src.getIdentifier()).getList(TypeToken.of(String.class));
            } catch (ObjectMappingException e) {
                //Empty
            }
        }

        {
            //If they HAVE referred someone but currently are not
            if (referred.isEmpty() || rootNode.getNode("referrals", src.getIdentifier()).isVirtual()){
                Utils.sendMessage(src, "&cYou haven't referred anyone!");
                return CommandResult.success();
            }
        }

        rankUpperParser rankUpperParser = new rankUpperParser();
        {
            referred.forEach(s -> {
                try {
                    int playTime = rankUpperParser.getPlayTime(s);

                    if (playTime >= 360) {


                        Optional<Player> referredPlayer = Sponge.getServer().getPlayer(UUID.fromString(s));

                        {
                            if (!referredPlayer.isPresent()) {
                                Utils.sendMessage(src, "&c" + Utils.getUsername(s) + " is not online! Rewards will be given once both players are online.");
                            } else {
                                Sponge.getCommandManager().process(Sponge.getServer().getConsole(), "cratekey give " + Utils.getUsername(s) + " master 1");
                                Sponge.getCommandManager().process(Sponge.getServer().getConsole(), "cratekey give " + src.getName() + " master 1");
                                Utils.sendMessage(src, "You have been awarded for referring " + Utils.getUsername(s));
                                Utils.sendMessage(referredPlayer.get(), "&aYou have been awared for " + src.getName() + " successfully referring! \n\n");
                            }
                        }

                    } else {
                        Utils.sendMessage(src, "&c" + Utils.getUsername(s) + " has not played for 6 hours");
                    }
                } catch (Exception e) {/*Empty*/}
            });

        }




        return CommandResult.success();
    }
}
