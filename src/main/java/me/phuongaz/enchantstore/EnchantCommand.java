package me.phuongaz.enchantstore;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public class EnchantCommand extends Command{
    
    public EnchantCommand(){
        super("enchanter", "Buy enchant");
        this.setPermission("enchanter.use");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args){
        if(sender instanceof Player){
            FormStorage.sendStartForm((Player) sender);
        }
        return true;
    }
}
