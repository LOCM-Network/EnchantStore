package me.phuongaz.enchantstore;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.utils.TextFormat;
import me.onebone.economyapi.EconomyAPI;
import net.minidev.asm.ex.ConvertException;
import ru.contentforge.formconstructor.form.CustomForm;
import ru.contentforge.formconstructor.form.ModalForm;
import ru.contentforge.formconstructor.form.SimpleForm;
import ru.contentforge.formconstructor.form.element.Input;

import java.util.concurrent.atomic.AtomicInteger;

public class FormStorage {
    
    public static void sendStartForm(Player player){
        SimpleForm form = new SimpleForm(TextFormat.colorize("§l§dCửa Hàng Phù Phép"));
        for(Integer enchant : Loader.getInstance().getEnchants().keySet()){
            Enchantment enchantment = Enchantment.getEnchantment(enchant);
            form.addButton(TextFormat.colorize("§l§f⚫§0 " + enchantment.getName() + " §l§f⚫"), (p, button) -> {
                confirmForm(player, enchant);
            });
        }
        form.send(player);
    }

    private static void confirmForm(Player player, Integer enchant){
        Enchantment enchantment = Enchantment.getEnchantment(enchant);
        AtomicInteger price = new AtomicInteger(Loader.getInstance().getEnchants().get(enchant));
        CustomForm form = new CustomForm(TextFormat.colorize("§l§aXác Nhận Phù Phép"));
        form.addElement(TextFormat.colorize("§l一 §fPhù phép: §e" + enchantment.getName()));
        form.addElement(TextFormat.colorize("§l一 §fCấp độ tối đa: " + enchantment.getMaxLevel()));
        form.addElement(TextFormat.colorize("§l一 §fLưu ý: cấp độ không cộng dồn"));
        form.addElement("level", new Input(TextFormat.colorize("§l一§f Giá:§e " + price + "§7/§e 1 §fcấp độ")));
        form.setNoneHandler((p) -> sendStartForm(player));
        form.setHandler((p, response) -> {
            String lvl = response.getInput("level").getValue();
            try{
                int level = Integer.parseInt(lvl);
                if (level < 0) {
                    player.sendMessage(TextFormat.colorize("§cSố cấp độ nhập vào không hợp lệ"));
                    return;
                }
                Enchantment currentEnchant = player.getInventory().getItemInHand().getEnchantment(enchantment.getId());
                if(currentEnchant != null){
                    int currentLevel = currentEnchant.getLevel();
                    // + 35% price for each level
                    price.set((int) (price.get() * (1 + 0.35 * currentLevel)));

                    if(level + currentLevel > enchantment.getMaxLevel()) {
                        player.sendMessage(TextFormat.colorize("§cPhù phép không thành công, cấp độ phù phép vượt quá cấp độ tối đa"));
                        return;
                    }

                    int totalLevel = level + currentLevel;
                    ModalForm modal = new ModalForm(TextFormat.colorize("§l§aXác Nhận Phù Phép"));
                    modal.setContent(TextFormat.colorize(
                            "§l§fPhù phép §e" + enchantment.getName() + " §fđã có cấp độ §e" + currentLevel + "§f.\n" +
                            "§l§fCấp độ phù phép mới: §e" + totalLevel + "§f.\n" +
                            "§l§fGiá phù phép: §e" + (level * price.get()) + "§f xu.\n"
                    ));

                    modal.setPositiveButton("§l§aXác Nhận");
                    modal.setNegativeButton("§l§cHủy");
                    modal.setHandler((p1, response1) -> {
                        if(response1){
                            if(EconomyAPI.getInstance().myMoney(player) >= level * price.get()){
                                EconomyAPI.getInstance().reduceMoney(player, level * price.get());
                                player.sendMessage(TextFormat.colorize("§l§aPhù phép thành công!"));
                                Item item = player.getInventory().getItemInHand();
                                item.addEnchantment(enchantment.setLevel(level + currentLevel));
                                player.getInventory().setItemInHand(item);
                            }else{
                                player.sendMessage(TextFormat.colorize("&cPhù phép không thành công, cần §e"+ (price.get() + level) + " §fxu"));
                            }
                        }
                    });
                    modal.send(player);
                }else{
                    if(EconomyAPI.getInstance().myMoney(player) >= level * price.get()){
                        EconomyAPI.getInstance().reduceMoney(player, level * price.get());
                        player.sendMessage(TextFormat.colorize("§l§aPhù phép thành công!"));
                        Item item = player.getInventory().getItemInHand();
                        item.addEnchantment(enchantment.setLevel(level));
                        player.getInventory().setItemInHand(item);
                    }else{
                        player.sendMessage(TextFormat.colorize("&cPhù phép không thành công, cần §e"+ (price.get() + level) + " §fxu"));
                    }
                }
            }catch(ConvertException exception){
                player.sendMessage(TextFormat.colorize("§cVui lòng nhập 'cấp độ' là số"));
            }
        });
        form.send(player);
    }
}
