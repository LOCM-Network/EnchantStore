package me.phuongaz.enchantstore;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.utils.TextFormat;
import me.onebone.economyapi.EconomyAPI;
import net.minidev.asm.ex.ConvertException;
import ru.contentforge.formconstructor.form.CustomForm;
import ru.contentforge.formconstructor.form.SimpleForm;
import ru.contentforge.formconstructor.form.element.Input;

public class FormStorage {
    
    public static void sendStartForm(Player player){
        SimpleForm form = new SimpleForm(TextFormat.colorize("Enchants"));
        for(Integer enchant : Loader.getInstance().getEnchants().keySet()){
            Enchantment enchantment = Enchantment.getEnchantment(enchant);
            form.addButton(TextFormat.colorize("" + enchantment.getName() + ""), (p, button) -> {
                confirmForm(player, enchant);
            });
        }
        form.send(player);
    }

    private static void confirmForm(Player player, Integer enchant){
        Enchantment enchantment = Enchantment.getEnchantment(enchant);
        int price = Loader.getInstance().getEnchants().get(enchant);
        CustomForm form = new CustomForm(TextFormat.colorize("CONFIRM"));
        form.addElement(TextFormat.colorize("Enchant" + enchantment.getName()));
        form.addElement(TextFormat.colorize("Max enchant: " + enchantment.getMaxLevel()));
        form.addElement("level", new Input(TextFormat.colorize(price + "/ 1 Level")));
        form.setNoneHandler((p) -> sendStartForm(player));
        form.setHandler((p, response) -> {
            String lvl = response.getInput("level").getValue();
            try{
                int level = Integer.parseInt(lvl);
                if(EconomyAPI.getInstance().myMoney(player) >= level * price){
                    EconomyAPI.getInstance().reduceMoney(player, level * price);
                    player.sendMessage(TextFormat.colorize("Enchant succesfully"));
                    Item item = player.getInventory().getItemInHand();
                    item.addEnchantment(enchantment.setLevel(level));
                    player.getInventory().setItemInHand(item);
                }else{
                    player.sendMessage(TextFormat.colorize("&cNot enough money!, need"+ (price + level) + " xu"));
                }
            }catch(ConvertException exception){
                player.sendMessage(TextFormat.colorize("Level must be a integer"));
            }
        });
        form.send(player);
    }
}
