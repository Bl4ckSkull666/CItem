/*
 * Decompiled with CFR 0_114.
 */
package de.bl4ckskull666.citem.utils;

import de.bl4ckskull666.citem.CItem;
import java.util.UUID;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class Util {
    public static boolean isNumeric(String str) {
        try {
            int i = Integer.parseInt(str);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isFloat(String str) {
        try {
            float i = Float.parseFloat(str);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isDouble(String str) {
        try {
            double i = Double.parseDouble(str);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static String getItemName(Player p, ItemStack item) {
        if(item.hasItemMeta()) {
            if(item.getItemMeta().hasDisplayName())
                return item.getItemMeta().getDisplayName();
        }
        
        return CItem.getLM().getText(p.getUniqueId(), "item-name." + item.getType().name().toLowerCase(), UpperFirst(item.getType().name().replace("_", " ")));
    }
    
    public static String getItemName(CommandSender s, ItemStack item) {
        if(item.hasItemMeta()) {
            if(item.getItemMeta().hasDisplayName())
                return item.getItemMeta().getDisplayName();
        }
        
        UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        if(s instanceof Player)
            uuid = ((Player)s).getUniqueId();
        
        return CItem.getLM().getText(uuid, "item-name." + item.getType().name().toLowerCase(), UpperFirst(item.getType().name().replace("_", " ")));
    }
    
    public static String UpperFirst(String str) {
        String[] t = str.split(" ");
        String tmp = String.valueOf(t[0].charAt(0)).toUpperCase() + t[0].substring(1);
        if(t.length == 1)
            return tmp;
        
        for(int i = 1; i < t.length; i++)
            tmp += " " + String.valueOf(t[i].charAt(0)).toUpperCase() + t[i].substring(1);
        
        return tmp;
    }
}

