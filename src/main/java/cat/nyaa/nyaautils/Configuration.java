package cat.nyaa.nyaautils;

import cat.nyaa.utils.BasicItemMatcher;
import cat.nyaa.utils.ISerializable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Configuration implements ISerializable {

    @Serializable
    public String language = "en_US";

    public List<BasicItemMatcher> enchantSrc = new ArrayList<>();
    public HashMap<Enchantment, Integer> enchantMaxLevel = new HashMap<>();

    private final NyaaUtils plugin;

    public Configuration(NyaaUtils plugin) {
        this.plugin = plugin;
    }

    public void save() {
        serialize(plugin.getConfig());
        plugin.saveConfig();
    }

    @Override
    public void deserialize(ConfigurationSection config) {
        ISerializable.deserialize(config, this);

        enchantSrc = new ArrayList<>();
        if (config.isConfigurationSection("enchantSrc")) {
            ConfigurationSection src = config.getConfigurationSection("enchantSrc");
            for (String key : src.getKeys(false)) {
                if (src.isConfigurationSection(key)) {
                    BasicItemMatcher tmp = new BasicItemMatcher();
                    tmp.deserialize(src.getConfigurationSection(key));
                }
            }
        }
        
        enchantMaxLevel = new HashMap<>();
        if (config.isConfigurationSection("enchant")) {
            ConfigurationSection list = config.getConfigurationSection("enchant");
            for (Enchantment e : Enchantment.values()) {
                if (e.getName().equalsIgnoreCase("Custom Enchantment")) {
                    continue;
                }
                enchantMaxLevel.put(e, list.getInt(e.getName() + ".MaxLevel", e.getMaxLevel()));
            }
        }
    }

    @Override
    public void serialize(ConfigurationSection config) {
        ISerializable.serialize(config, this);

        ConfigurationSection dst = config.createSection("enchantSrc");
        int idx = 0;
        for (BasicItemMatcher m : enchantSrc) {
            m.serialize(dst.createSection(Integer.toString(idx)));
            idx++;
        }
        
        ConfigurationSection list = config.createSection("enchant");
        for (Enchantment k : enchantMaxLevel.keySet()) {
            list.set(k.getName() + ".MaxLevel", enchantMaxLevel.get(k));
        }
    }
}
