package me.pokerman99.referralsec.commands;

import com.google.common.reflect.TypeToken;
import me.pokerman99.referralsec.Main;
import me.pokerman99.referralsec.Utils;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.manipulator.mutable.entity.JoinData;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.profile.GameProfileManager;
import org.spongepowered.api.service.user.UserStorageService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;

import static me.pokerman99.referralsec.Main.referredNames;
import static me.pokerman99.referralsec.Main.rootNode;

public class referCommand implements CommandExecutor {


    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String name = args.<String>getOne("name").get();

        GameProfileManager profileManager = Sponge.getServer().getGameProfileManager();
        GameProfile gameProfile;
        String refName = null; //This is needed because it's initialized in a try catch
        String refUUID;
        List<String> referredLocalNames = new ArrayList<>();

        UserStorageService userStorageService = Sponge.getServiceManager().provide(UserStorageService.class).get();
        User user;

        {//See if the account exists
            try {
                gameProfile = profileManager.get(name).get();
                refName = gameProfile.getName().get();
            } catch (ExecutionException | InterruptedException e) {
                Utils.sendMessage(src, Main.referCommandPlayerNotFound);
                return CommandResult.empty();
            }
        }


        {//It needs to poll the name once thus once the isPresent is called I can grab the user object makes no sense but what ever
            if (userStorageService.get(refName).isPresent()) {
                user = userStorageService.get(refName).get();
                refUUID = user.getIdentifier();
            } else {
                user = userStorageService.get(refName).get();
                refUUID = user.getIdentifier();
            }
        }

        {//Check if the user has played before & if they've been referred already
            try {
                user.get(JoinData.class).get();
                Utils.sendMessage(src, Main.referCommandHasPlayedBefore);
                return CommandResult.empty();
            }  catch (NoSuchElementException e) {/*Do nothing*/}

            if (referredNames.contains(refUUID)) {
                Utils.sendMessage(src, Main.referCommandAlreadyReferred);
                return CommandResult.empty();
            }
        }

        {//Grab all the people they've referred
            try {
                rootNode.getNode("referrals", src.getIdentifier()).getList(TypeToken.of(String.class)).forEach(s -> referredLocalNames.add(s));
            } catch (ObjectMappingException e) {
                e.printStackTrace();
            }
            referredNames.add(refUUID);
            referredLocalNames.add(refUUID);
        }

        rootNode.getNode("referrals", src.getIdentifier()).setValue(referredLocalNames);
        rootNode.getNode("limbo").setValue(referredNames);

        Main.getInstance().save();

        Utils.sendMessage(src, Main.referCommandSuccessfullyReferred.replace("%player%", refName));
        return CommandResult.success();
    }
}
