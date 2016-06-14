package de.bl4ckskull666.citem;

import de.bl4ckskull666.citem.classes.BookData;
import de.bl4ckskull666.citem.classes.LanguageManager;
import de.bl4ckskull666.citem.utils.Util;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class CItem
extends JavaPlugin {
    private static CItem _p;
    private static LanguageManager _lm;

    @Override
    public void onEnable() {
        _p = this;
        if(!getDataFolder().exists()) {
            getDataFolder().mkdir();
            getConfig().options().copyDefaults(true);
        }
        saveConfig();
        BookData.load();
        getCommand("give").setExecutor(new CommandGive());
        //getCommand("unlockbook").setExecutor(new CommandUnlockBook());
        _lm = new LanguageManager();
    }

    @Override
    public void onDisable() {
        BookData.save();
    }

    public static CItem getPlugin() {
        return _p;
    }

    public static String getString(String path, String def) {
        if (!_p.getConfig().isString(path)) {
            _p.getConfig().set(path, def);
            _p.saveConfig();
            return ChatColor.translateAlternateColorCodes('&', def);
        }
        return ChatColor.translateAlternateColorCodes('&', _p.getConfig().getString(path));
    }

    public static int getInt(String path, int def) {
        if (!_p.getConfig().isInt(path)) {
            _p.getConfig().set(path, (Object)def);
            _p.saveConfig();
            return def;
        }
        return _p.getConfig().getInt(path);
    }

    public static boolean getBoolean(String path, boolean def) {
        if (!_p.getConfig().isBoolean(path)) {
            _p.getConfig().set(path, (Object)def);
            _p.saveConfig();
            return def;
        }
        return _p.getConfig().getBoolean(path);
    }

    public static LanguageManager getLM() {
        return _lm;
    }

    public static boolean hasEnoughSize(Inventory inv, int need) {
        int free = 0;
        for(ItemStack item: inv.getContents()) {
            if(item != null) continue;
            free++;
        }
        return free >= need;
    }

    public static boolean isItem(String str) {
        return CItem.getItem(str) != null;
    }

    public static ItemStack getItem(String str) {
        String[] args = str.split(" ");
        if(args.length < 2) {
            _p.getLogger().log(Level.INFO, "{0} hat zu wenig Argumente Mindestens : Itemname Itemmenge - sind gefordert.", str);
            return null;
        }
        
        if(!Util.isNumeric(args[1])) {
            _p.getLogger().log(Level.INFO, "{0} muss eine Zahl sein", args[1]);
            return null;
        }
        
        String[] itemname = args[0].split("\\:");
        if(Material.matchMaterial((String)itemname[0]) == null) {
            _p.getLogger().log(Level.INFO, "{0} ist kein gultiges Item.", itemname[0]);
            return null;
        }
        
        ItemStack i = itemname.length >= 2 && Util.isNumeric(itemname[1]) ? new ItemStack(Material.matchMaterial(itemname[0]), Integer.parseInt(args[1]), Short.parseShort(itemname[1])) : new ItemStack(Material.matchMaterial(itemname[0]), Integer.parseInt(args[1]));
        i.setItemMeta(Bukkit.getItemFactory().getItemMeta(i.getType()));
        
        if(args.length >= 3) {
            block18:
            for(int a = 2; a < args.length; a++) {
                String[] sargs = args[a].split(":");
                if(sargs.length != 2)
                    continue;
                
                switch (sargs[0].toLowerCase()) {
                    case "lore":
                        String[] msg = sargs[1].split("\\|");
                        if (msg.length > 4) continue block18;
                        ArrayList<String> lore = new ArrayList<>();
                        for(String msg1: msg)
                            lore.add(ChatColor.translateAlternateColorCodes((char)'&', msg1.replaceAll("_", " ")));
                        CItem.setLore(i, lore);
                        break;
                    case "name":
                        CItem.setItemName(i, sargs[1]);
                        break;
                    case "color":
                        CItem.setColor(i, sargs[1]);
                        break;
                    case "book":
                        CItem.setBook(i, sargs[1]);
                        break;
                    case "head":
                        CItem.setPlayerHead(i, sargs[1]);
                        break;
                    case "player":
                        CItem.checkMyName(i, sargs[1]);
                        break;
                    case "enchant":
                        if(!sargs[1].equalsIgnoreCase("min") && !sargs[1].equalsIgnoreCase("max"))
                            break;
                        
                        for(Enchantment en : Enchantment.values()) {
                            if(!en.canEnchantItem(i))
                                continue;
                            CItem.setEnchantment(i, en.getName(), sargs[1].equalsIgnoreCase("min") ? en.getStartLevel() : en.getMaxLevel());
                        }
                        break;
                    default:
                        if((Enchantment.getByName((String)sargs[0].toUpperCase()) != null || CItem.getEnchant(sargs[0]) != null) && Util.isNumeric(sargs[1])) {
                            CItem.setEnchantment(i, sargs[0], Integer.parseInt(sargs[1]));
                            break;
                        }
                        _p.getLogger().log(Level.INFO, "Ignore {0}:{1}", new Object[]{sargs[0], sargs[1]});
                }
            }
        }
        return i;
    }

    private static void setItemName(ItemStack i, String name) {
        ItemMeta im = i.getItemMeta();
        im.setDisplayName(ChatColor.translateAlternateColorCodes((char)'&', (String)name.replaceAll("_", " ")));
        i.setItemMeta(im);
    }

    private static void setLore(ItemStack i, List<String> lore) {
        ItemMeta im = i.getItemMeta();
        im.setLore(lore);
        i.setItemMeta(im);
    }

    private static void setColor(ItemStack i, String color) {
        if (i.getItemMeta() instanceof LeatherArmorMeta) {
            LeatherArmorMeta lam = (LeatherArmorMeta)i.getItemMeta();
            String[] rgb = color.split("\\,");
            if (rgb.length == 3 && Util.isNumeric(rgb[0]) && Util.isNumeric(rgb[1]) && Util.isNumeric(rgb[2]) && Integer.parseInt(rgb[0]) >= 0 && Integer.parseInt(rgb[0]) <= 255 && Integer.parseInt(rgb[1]) >= 0 && Integer.parseInt(rgb[1]) <= 255 && Integer.parseInt(rgb[2]) >= 0 && Integer.parseInt(rgb[2]) <= 255) {
                Color c = Color.fromRGB((int)Integer.parseInt(rgb[0]), (int)Integer.parseInt(rgb[1]), (int)Integer.parseInt(rgb[2]));
                lam.setColor(c);
            }
            i.setItemMeta((ItemMeta)lam);
        }
    }

    private static void checkMyName(ItemStack i, String name) {
        if (i.getItemMeta().hasDisplayName())
            i.getItemMeta().setDisplayName(i.getItemMeta().getDisplayName().replace("%player%", name));
        
        if (i.getItemMeta().hasLore()) {
            ArrayList<String> temp = new ArrayList<>();
            for(String str : i.getItemMeta().getLore())
                temp.add(str.replace("%player%", name));
            i.getItemMeta().setLore(temp);
        }
        
        if (i.getItemMeta() instanceof BookMeta) {
            BookMeta bm = (BookMeta)i.getItemMeta();
            ArrayList<String> temp = new ArrayList<>();
            for(String page : bm.getPages())
                temp.add(page.replace("%player%", name));
            bm.setPages(temp);
            i.setItemMeta(bm);
        }
    }

    private static void setPlayerHead(ItemStack i, String name) {
        if (!(i.getItemMeta() instanceof SkullMeta)) {
            return;
        }
        SkullMeta sm = (SkullMeta)i.getItemMeta();
        sm.setOwner(name);
        sm.setDisplayName(name);
        i.setItemMeta((ItemMeta)sm);
    }

    private static void setBook(ItemStack i, String book) {
        if(!BookData.isBook(book.toLowerCase()))
            return;
        
        if(!(i.getItemMeta() instanceof BookMeta))
            return;
        
        BookMeta bm = BookData.getBook(book.toLowerCase());
        if(bm != null)
            i.setItemMeta(bm);
    }

    private static void setEnchantment(ItemStack i, String ench, int lvl) {
        Enchantment e = CItem.getEnchant(ench);
        if (e == null) {
            return;
        }
        if (i.getType().equals((Object)Material.ENCHANTED_BOOK)) {
            EnchantmentStorageMeta esm = (EnchantmentStorageMeta)i.getItemMeta();
            esm.addStoredEnchant(e, lvl, true);
            if (!esm.hasStoredEnchant(e)) {
                _p.getLogger().log(Level.INFO, "Failed to add {0} to the EnchantmentStorageMeta.", ench);
            }
            i.setItemMeta((ItemMeta)esm);
        } else {
            ItemMeta im = i.getItemMeta();
            im.addEnchant(e, lvl, true);
            if (!im.hasEnchant(e)) {
                _p.getLogger().log(Level.INFO, "Failed to add {0} to the ItemMeta.", ench);
            }
            i.setItemMeta(im);
        }
    }

    private static Enchantment getEnchant(String name) {
        name = name.toLowerCase();
        Enchantment ench = null;
        if (_p.getConfig().isString("enchantments." + name)) {
            try {
                ench = Enchantment.getByName((String)_p.getConfig().getString("enchantments." + name).toUpperCase());
            } catch (Exception var2_2) {}
        }
        if (ench == null) {
            try {
                ench = Enchantment.getByName((String)name.toUpperCase());
            } catch (Exception var2_3) {}
        }
        return ench;
    }
}

