package me.phuongaz.enchantstore;

import java.util.LinkedHashMap;
import java.util.List;

import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.plugin.PluginBase;

public class Loader extends PluginBase{

    private static Loader _instance;
    private static LinkedHashMap<Integer, Integer> enchants = new LinkedHashMap<>();

    @Override
    public void onEnable(){
        _instance = this;
        saveDefaultConfig();
        if(!getConfig().exists("enchants")){
            List<String> ids = getConfig().getStringList("enchants");
            for(int i = 0; i >= 30; i++){
                Enchantment enchant = Enchantment.getEnchantment(i);
                if(enchant != null){
                    ids.add(enchant + ":10000");
                }
            } 
            getConfig().set("enchants", ids);   
            getConfig().save();      
            getConfig().reload();  
        }
        for(String id : getConfig().getStringList("enchants")){
            String[] parts = id.split(":");
            enchants.put(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        }
        getServer().getCommandMap().register("EnchantStore", new EnchantCommand());
    }

    public LinkedHashMap<Integer, Integer> getEnchants(){
        return enchants;
    }

    public static Loader getInstance(){
        return _instance;
    }
    
}