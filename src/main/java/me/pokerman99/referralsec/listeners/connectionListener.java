package me.pokerman99.referralsec.listeners;

import com.google.common.reflect.TypeToken;
import me.pokerman99.referralsec.Main;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import java.util.ArrayList;
import java.util.List;

import static me.pokerman99.referralsec.Main.referredNames;
import static me.pokerman99.referralsec.Main.rootNode;

public class connectionListener {

    @Listener
    public void onJoin(ClientConnectionEvent.Join event) {
       /* if (referredNames.containsKey(event.getTargetEntity().getName())) {
            Player player = event.getTargetEntity();

            //Seeing if they have ever referred someone
            if (rootNode.getNode("referrals", referredNames.get(player.getName())).isVirtual()) {

                {
                    List<String> referred = new ArrayList<>();
                    referred.add(player.getIdentifier());
                    rootNode.getNode("referrals", referredNames.get(player.getName())).setValue(referred);

                    referredNames.remove(player.getName(), referredNames.get(player.getName()));

                    rootNode.getNode("limbo").setValue(referredNames);

                    Main.getInstance().save();
                }

            } else { //If they have referred someone grab the list


                {
                    try {

                        List<String> referred = rootNode.getNode("referrals", referredNames.get(player.getName())).getList(TypeToken.of(String.class));
                        referred.add(player.getIdentifier());
                        rootNode.getNode("referrals", referredNames.get(player.getName())).setValue(referred);

                        referredNames.remove(player.getName(), referredNames.get(player.getName()));

                        Main.getInstance().save();

                    } catch (ObjectMappingException e) {
                        e.printStackTrace();
                    }
                }


            }

        }*/
    }

}
