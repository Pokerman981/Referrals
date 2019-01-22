package me.pokerman99.referralsec.commands;

import com.google.common.reflect.TypeToken;
import me.pokerman99.referralsec.Main;
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

import static me.pokerman99.referralsec.Main.referredNames;
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
                Utils.sendMessage(src, Main.claimCommandNoReferees);
                return CommandResult.success();
            }
        }

        {
            for (String s : referred) {
                try {
                    int playTime = Main.rankUpperParser.getPlayTime(s);

                    if (playTime >= 360) {

                        Optional<Player> referredPlayer = Sponge.getServer().getPlayer(UUID.fromString(s));

                        {
                            if (referredPlayer.isPresent()) {
                                Sponge.getCommandManager().process(Sponge.getServer().getConsole(), Main.claimCommandCommand1.replace("%player%", src.getName()));
                                Sponge.getCommandManager().process(Sponge.getServer().getConsole(), Main.claimCommandCommand2.replace("%player%", Utils.getUsername(s)));
                                //They are both online
                                Utils.sendMessage(src, Main.claimCommandSuccessfulReferralPlayer1.replace("%player%", Utils.getUsername(s)));
                                Utils.sendMessage(referredPlayer.get(), Main.claimCommandSuccessfulReferralPlayer2.replace("%player%", src.getName()));

                                referred.remove(s);
                                referredNames.remove(s);
                                rootNode.getNode("referrals", src.getIdentifier()).setValue(referred);
                                rootNode.getNode("limbo").setValue(referredNames);

                            } else {
                                Sponge.getCommandManager().process(Sponge.getServer().getConsole(), Main.claimCommandCommand1.replace("%player%", src.getName()));
                                Utils.sendMessage(src, Main.claimCommandSuccessfulReferralPlayer1.replace("%player%", Utils.getUsername(s)));
                                //Commands to queue
                                rootNode.getNode("queued-operations", s, "command").setValue(Main.claimCommandCommand2.replace("%player%", Utils.getUsername(s)));
                                rootNode.getNode("queued-operations", s, "message").setValue(Main.claimCommandSuccessfulReferralPlayer2.replace("%player%", src.getName()));

                                referred.remove(s);
                                referredNames.remove(s);
                                rootNode.getNode("referrals", src.getIdentifier()).setValue(referred);
                                rootNode.getNode("limbo").setValue(referredNames);

                            }

                            int successfulreferrals = rootNode.getNode("successfully-referred").getInt();
                            rootNode.getNode("successfully-referred").setValue(successfulreferrals + 1);

                            Main.getInstance().save();

                            return CommandResult.success();
                        }

                    }
                } catch (Exception e) {/*Empty*/ e.printStackTrace();}
            }

            Utils.sendMessage(src, "&cYou can't claim any referral rewards yet!");
        }




        return CommandResult.success();
    }
}
