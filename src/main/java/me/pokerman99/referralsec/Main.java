package me.pokerman99.referralsec;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import me.pokerman99.referralsec.commands.checkCommand;
import me.pokerman99.referralsec.commands.claimCommand;
import me.pokerman99.referralsec.commands.referCommand;
import me.pokerman99.referralsec.commands.reloadCommand;
import me.pokerman99.referralsec.listeners.connectionListener;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;

@Plugin(
        id = "referralsec",
        name = "ReferralsEC",
        version = "1.0",
        description = ""
    )

public class Main {

    @Inject
    @DefaultConfig(sharedRoot = false)
    public Path defaultConfig;

    @Inject
    @DefaultConfig(sharedRoot = false)
    public ConfigurationLoader<CommentedConfigurationNode> loader;

    @Inject
    @ConfigDir(sharedRoot = false)
    public Path ConfigDir;

    @Inject
    public PluginContainer plugin;
    public PluginContainer getPlugin() {
        return this.plugin;
    }

    public static CommentedConfigurationNode rootNode;

    public static Main instance;

    public static Main getInstance(){
        return instance;
    }

    @Inject
    private Logger logger;

    public Logger getLogger() {
        return logger;
    }

    public static List<String> referredNames = new ArrayList<>();


    @Listener
    public void onInit(GameInitializationEvent event) throws IOException {

        {
            rootNode = loader.load();
            instance = this;
        }

        if (!defaultConfig.toFile().exists()) {
            generateDefaultConfig();
        }

        {
            populateVariables();
            registerCommands();
            registerListeners();
        }

        try {
            rootNode.getNode("limbo").getList(TypeToken.of(String.class)).forEach(s -> referredNames.add(s));
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }

        //Idk if I even want this here
        getLogger().info("Finished Init Event");
    }

    void registerCommands() {
        CommandSpec claimCommand = CommandSpec.builder()
                .permission("referralsec.claim")
                .executor(new claimCommand())
                .build();

        CommandSpec referCommand = CommandSpec.builder()
                .permission("referralsec.refer")
                .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("name"))))
                .executor(new referCommand())
                .build();

        CommandSpec checkCommand = CommandSpec.builder()
                .permission("referralsec.check")
                .executor(new checkCommand())
                .build();

        CommandSpec reloadCommand = CommandSpec.builder()
                .permission("referralsec.reload")
                .executor(new reloadCommand())
                .build();

        CommandSpec referralCommand = CommandSpec.builder()
                .permission("referralsec.main")
                .child(referCommand, "refer")
                .child(checkCommand, "check")
                .child(claimCommand, "claim")
                .child(reloadCommand, "reload")
                .build();

        Sponge.getCommandManager().register(instance, referralCommand, "referrals");
        //They can use the main command or the stand alone command, they do the same thing
        Sponge.getCommandManager().register(instance, referCommand, "refer");
    }

    void registerListeners() {
        Sponge.getEventManager().registerListeners(instance, new connectionListener());
    }

    public static String referCommandPlayerNotFound;
    public static String referCommandHasPlayedBefore;
    public static String referCommandAlreadyReferred;
    public static String referCommandSuccessfullyReferred;

    public static String claimCommandNoReferees;
    public static String claimCommandSuccessfulReferralPlayer1;
    public static String claimCommandSuccessfulReferralPlayer2;

    public static String checkCommandNoReferees;
    public static String checkCommandStringBuilderHeader;

    public static String claimCommandCommand1;
    public static String claimCommandCommand2;

    void generateDefaultConfig() {
        rootNode.getNode("config-version").setValue(1.0);
        rootNode.getNode("successfully-referred").setValue(0);
        rootNode.getNode("limbo").setValue(referredNames);

        CommentedConfigurationNode refercommandmessages = rootNode.getNode("data", "messages", "refer-command");
        {
            refercommandmessages.getNode("player-not-found").setValue("&cCan't find the player! Make sure the spelling is correct.");
            refercommandmessages.getNode("has-played-before").setValue("&cThis player has joined before!");
            refercommandmessages.getNode("already-referred").setValue("&cThis player has already been referred!");
            refercommandmessages.getNode("successfully-referred").setValue("&aYou have successfully referred, %player%\n\nOnce %player% has played for 6 hours you will both recieve a master key!");
        }

        CommentedConfigurationNode claimcommandmessages = rootNode.getNode("data", "messages", "claim-command");
        {
            claimcommandmessages.getNode("no-referees").setValue("&cYou have not referred anyone!");
            claimcommandmessages.getNode("successful-referral-player1").setValue("&aYou have been awarded for referring %player%");
            claimcommandmessages.getNode("successful-referral-player2").setValue("&aYou have been awarded for %player% successfully referring you!");
        }

        CommentedConfigurationNode checkcommandmessages = rootNode.getNode("data", "messages", "check-command");
        {
            checkcommandmessages.getNode("no-referees").setValue("&cYou have not referred anyone!");
            checkcommandmessages.getNode("stringbuilder-header").setValue("&aPeople you've referred\n\n");
        }

        CommentedConfigurationNode claimcommandcommands = rootNode.getNode("data", "commands", "claim-command");
        {
            claimcommandcommands.getNode("successful-referral-player1").setValue("cratekey give %player% master 1");
            claimcommandcommands.getNode("successful-referral-player2").setValue("cratekey give %player% master 1");
        }

        save();
    }


    public void populateVariables() {
        CommentedConfigurationNode refercommandmessages = rootNode.getNode("data", "messages", "refer-command");
        {
            referCommandPlayerNotFound = refercommandmessages.getNode("player-not-found").getString();
            referCommandHasPlayedBefore = refercommandmessages.getNode("has-played-before").getString();
            referCommandAlreadyReferred = refercommandmessages.getNode("already-referred").getString();
            referCommandSuccessfullyReferred = refercommandmessages.getNode("successfully-referred").getString();
        }

        CommentedConfigurationNode claimcommandmessages = rootNode.getNode("data", "messages", "claim-command");
        {
            claimCommandNoReferees = claimcommandmessages.getNode("no-referees").getString();
            claimCommandSuccessfulReferralPlayer1 = claimcommandmessages.getNode("successful-referral-player1").getString();
            claimCommandSuccessfulReferralPlayer2 = claimcommandmessages.getNode("successful-referral-player2").getString();
        }

        CommentedConfigurationNode checkcommandmessages = rootNode.getNode("data", "messages", "check-command");
        {
            checkCommandNoReferees = checkcommandmessages.getNode("no-referees").getString();
            checkCommandStringBuilderHeader = checkcommandmessages.getNode("stringbuilder-header").getString();
        }

        CommentedConfigurationNode claimcommandcommands = rootNode.getNode("data", "commands", "claim-command");
        {
            claimCommandCommand1 = claimcommandcommands.getNode("successful-referral-player1").getString();
            claimCommandCommand2 = claimcommandcommands.getNode("successful-referral-player2").getString();
        }
    }



























    public void save() {
        try {
            loader.save(rootNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
