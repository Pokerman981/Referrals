package me.pokerman99.referralsec;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import me.pokerman99.referralsec.commands.checkCommand;
import me.pokerman99.referralsec.commands.claimCommand;
import me.pokerman99.referralsec.commands.referCommand;
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

        CommandSpec referralCommand = CommandSpec.builder()
                .permission("referralec.main")
                .child(referCommand, "refer")
                .child(checkCommand, "check")
                .child(claimCommand, "claim")
                .build();

        Sponge.getCommandManager().register(instance, referralCommand, "referrals");
        //They can use the main command or the stand alone command, they do the same thing
        Sponge.getCommandManager().register(instance, referCommand, "refer");
    }

    void registerListeners() {
        Sponge.getEventManager().registerListeners(instance, new connectionListener());
    }

    void generateDefaultConfig() {
        rootNode.getNode("config-version").setValue(1.0);
        rootNode.getNode("successfuly-referred").setValue(0);
        rootNode.getNode("limbo").setValue(referredNames);

        save();
    }



























public void save() {
        try {
            loader.save(rootNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
