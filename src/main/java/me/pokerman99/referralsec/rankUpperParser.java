package me.pokerman99.referralsec;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class rankUpperParser {

    private File file;
    private ConfigurationNode ruConfig;
    private ConfigurationLoader<CommentedConfigurationNode> ruLoader;

    public rankUpperParser() {
        try {
            setFile(new File(Main.getInstance().ConfigDir.toFile().getParentFile(), "rankupper/playerstats.conf"));

            ruLoader = HoconConfigurationLoader.builder().setFile(getFile()).build();
            ruConfig = ruLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }




    public int getPlayTime(String uuid) throws IOException {
        return ruConfig.getNode(uuid, "TimePlayed").getInt();
    }

}
