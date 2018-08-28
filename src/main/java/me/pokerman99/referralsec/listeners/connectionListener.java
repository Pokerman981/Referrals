package me.pokerman99.referralsec.listeners;

import me.pokerman99.referralsec.Main;
import me.pokerman99.referralsec.Utils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import static me.pokerman99.referralsec.Main.rootNode;

public class connectionListener {

    @Listener
    public void onJoin(ClientConnectionEvent.Join event) {
        if (!rootNode.getNode("queued-operations", event.getTargetEntity().getIdentifier()).isVirtual()) {
            Utils.sendMessage(event.getTargetEntity(), rootNode.getNode("queued-operations", event.getTargetEntity().getIdentifier(), "message").getString());
            Sponge.getCommandManager().process(Sponge.getServer().getConsole(), rootNode.getNode("queued-operations", event.getTargetEntity().getIdentifier(), "command").getString());

            rootNode.getNode("queued-operations", event.getTargetEntity().getIdentifier()).setValue(null);

            Main.getInstance().save();
        }
    }

}
