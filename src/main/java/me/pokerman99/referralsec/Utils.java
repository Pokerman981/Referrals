package me.pokerman99.referralsec;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.profile.GameProfileManager;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.UUID;

public class Utils {

    public static String color(String string) {
        return TextSerializers.FORMATTING_CODE.serialize(Text.of(string));
    }

    public static void sendMessage(CommandSource sender, String message) {
        if (sender == null) { return; }
        sender.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(color(message)));
    }

    public static String getUsername(String uuid) throws Exception{
        GameProfileManager profileManager = Sponge.getServer().getGameProfileManager();
        return profileManager.get(UUID.fromString(uuid)).get().getName().get();
    }

}
