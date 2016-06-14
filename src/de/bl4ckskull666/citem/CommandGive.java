package de.bl4ckskull666.citem;

import de.bl4ckskull666.citem.classes.BookData;
import de.bl4ckskull666.citem.utils.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class CommandGive implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        Player p = null;
        String itemStr = "";
        UUID puuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        if(s instanceof Player) {
            p = (Player)s;
            puuid = p.getUniqueId();
        }
        
        if(a.length == 0) {
            viewHelpMenu(s, puuid, a, c);
            return true;
        }
        if(a.length >= 1) {
            if(options(s, puuid, a, c))
                return true;
            
            for(String strA : a) {
                if(Bukkit.getPlayer((String)strA) != null) {
                    p = Bukkit.getPlayer((String)strA);
                    continue;
                }
                if(strA.equalsIgnoreCase("me") && s instanceof Player) {
                    p = (Player)s;
                    continue;
                }
                itemStr = itemStr + (itemStr.isEmpty()?strA:new StringBuilder().append(" ").append(strA).toString());
            }
        }
        
        if(p != null && !p.getName().equalsIgnoreCase(s.getName())) {
            if(!s.hasPermission("citem.use.give.other")) {
                CItem.getLM().sendMessage(s, "cmd.give.no-perm-other", "You don't have permission to give Items to other players directly.");
                return true;
            }
        } else if(s instanceof Player && p == null || s instanceof Player && p.getName().equalsIgnoreCase(s.getName())) {
            p = (Player)s;
            if(!p.hasPermission("citem.use.give")) {
                CItem.getLM().sendMessage(s, "cmd.give.no-perm", "You don't habe permission to use this command.");
                return true;
            }
        } else {
            CItem.getLM().sendMessage(s, "cmd.give.unknown", "Unknown error?! What do you have do?");
            return true;
        }
        
        if(itemStr.isEmpty()) {
            CItem.getLM().sendMessage(s, "cmd.give.no-material", "Can't find a {Material Amount} in your string.");
            return true;
        }
        
        ItemStack item = CItem.getItem(itemStr);
        if(item == null) {
            CItem.getLM().sendMessage(s, "cmd.give.no-item", "Your given options not a available Item.");
            return true;
        }
        
        if(CItem.getBoolean("need-material-permission", true) && !p.hasPermission("citem.use.material." + item.getType().name().toLowerCase())) {
            CItem.getLM().sendMessage(p, "cmd.give.no-perm-material", "You don't have permission to use this Material.");
            return true;
        }
        
        if(p.getInventory().firstEmpty() == -1) {
            if(p.getName().equalsIgnoreCase(s.getName()))
                CItem.getLM().sendMessage(s, "cmd.give.more-inv-self", "You need more place in your Inventory.");
            else
                CItem.getLM().sendMessage(s, "cmd.give.more-inv-other", "%name% need's more place in the inventory first.", new String[] {"%name%"}, new String[] {p.getName()});
        }
        
        if(item.getAmount() > item.getMaxStackSize()) {
            ItemStack tmpItem = item.clone();
            tmpItem.setAmount(1);
            for(int i = 0; i < item.getAmount(); i++) {
                if(p.getInventory().firstEmpty() <= -1) {
                    int lost = item.getAmount() - i;
                    if(s.getName().equalsIgnoreCase(p.getName()))
                        CItem.getLM().sendMessage(s, "cmd.give.more-inv-self-lost", "You need more place in your Inventory. Lost %lost% items.", new String[] {"%lost%"}, new String[] {String.valueOf(lost)});
                    else
                        CItem.getLM().sendMessage(s, "cmd.give.more-inv-other-lost", "%name% has no more place in the Inventory. Lost %lost% items.", new String[] {"%name%", "%lost%"}, new String[] {p.getName(), String.valueOf(lost)});
                    p.updateInventory();
                    return true;
                }
                p.getInventory().addItem(tmpItem.clone());
            }
        } else
            p.getInventory().addItem(item);
        p.updateInventory();
        
        if(s.getName().equalsIgnoreCase(p.getName())) {
            CItem.getLM().sendMessage(s, "cmd.give.successful.self", "You have placed %amount% of %name% in your Inventory.", new String[] {"%amount%", "%name%"}, new String[] {String.valueOf(item.getAmount()), Util.getItemName(s, item)});
        } else {
            CItem.getLM().sendMessage(s, "cmd.give.successful.to", "You have %amount% x %name% given to %player%", new String[] {"%player%", "%amount%", "%name%"}, new String[] {p.getName(), String.valueOf(item.getAmount()), Util.getItemName(s, item)});
            CItem.getLM().sendMessage(p, "cmd.give.successful.from", "%player% has given you %amount% x %name%", new String[] {"%player%", "%amount%", "%name%"}, new String[] {s.getName(), String.valueOf(item.getAmount()), Util.getItemName(p, item)});
        }
        return true;
    }

    private boolean options(CommandSender s, UUID uuid, String[] a, Command c) {
        if(a.length == 1 && Util.isNumeric(a[0])) {
            viewHelpMenu(s, uuid, a, c);
            return true;
        }
        
        int page = 1;
        String other = "";
        if(a.length >= 3 && Util.isNumeric(a[2])) {
            page = Integer.parseInt(a[2]);
            other = a[1];
        } else if(a.length >= 3 && Util.isNumeric(a[1])) {
            page = Integer.parseInt(a[1]);
            other = a[2];
        } else if(a.length >= 2 && Util.isNumeric(a[1])) 
            page = Integer.parseInt(a[1]);
        else if(a.length > 1)
            other = a[1];
        
        int i = 1;
        int maxPage = 1;
        switch (a[0].toLowerCase()) {
            case "material":
                if(!s.hasPermission("citem.use.material")) {
                    CItem.getLM().sendMessage(s, "cmd.material.no-perm", "You have no permission to listen the Materials.", new String[] {"%cmd%"}, new String[] {c.getName()});
                    return true;
                }
                
                ArrayList<Material> mats = new ArrayList<>();
                if(!other.isEmpty()) {
                    try {
                        for(Material m: Material.values()) {
                            if(m.name().toLowerCase().contains(other.toLowerCase()))
                                mats.add(m);
                        }
                    } catch(Exception ex) {}
                }
                
                if(mats.isEmpty())
                    mats.addAll(Arrays.asList(Material.values()));
                
                if(mats.size() > 10) {
                    maxPage = Math.max(1,(int)Math.round(((double)mats.size()/10.0d)+0.5d));
                    
                }
                if(page > maxPage) {
                    CItem.getLM().sendMessage(s, "cmd.material.high-page", "The given Page number is higher than the max page.", new String[] {"%cmd%"}, new String[] {c.getName()});
                    return true;
                }

                CItem.getLM().sendMessage(s, "cmd.material.header", "Here a list of all available Materials.", new String[] {"%cmd%"}, new String[] {c.getName()});
                for(Material mat: mats) {
                    if(i > (page*10-10) && i <= (page*10))
                        CItem.getLM().sendMessage(s, "cmd.material.body", "%id%. %name% | Max Stack: %stack% | Max.Dura: %dura%", new String[] {"%id%", "%name%", "%stack%", "%dura%"}, new String[] {String.valueOf(i), mat.name(), String.valueOf(mat.getMaxStackSize()), String.valueOf(mat.getMaxDurability())});
                    i++;
                }
                
                if(maxPage > 1)
                    CItem.getLM().sendMessage(s, "cmd.material.footer", "%page% of %pages% pages", new String[] {"%page%", "%pages%"}, new String[] {String.valueOf(page), String.valueOf(maxPage)});
                return true;
            case "enchant":
                if(!s.hasPermission("citem.use.enchant")) {
                    CItem.getLM().sendMessage(s, "cmd.enchant.no-perm", "You have not the right permissions to view enchantsments.", new String[] {"%cmd%"}, new String[] {c.getName()});
                    return true;
                }
                
                ArrayList<Enchantment> ench = new ArrayList<>();
                if(!other.isEmpty()) {
                    try {
                        for(Enchantment en: Enchantment.values()) {
                            if(en.getName().toLowerCase().contains(other.toLowerCase()))
                                ench.add(en);
                        }
                    } catch(Exception ex) {}
                }
                
                if(ench.isEmpty())
                    ench.addAll(Arrays.asList(Enchantment.values()));
                
                maxPage = Math.max(1,(int)Math.round(((double)ench.size()/10.0d)+0.5d));
                if(page > maxPage) {
                    CItem.getLM().sendMessage(s, "cmd.enchant.high-page", "Your given page number is higher than the maximum.", new String[] {"%cmd%"}, new String[] {c.getName()});
                    return true;
                }
                
                CItem.getLM().sendMessage(s, "cmd.enchant.header", "List of all available Enchantments:", new String[] {"%cmd%"}, new String[] {c.getName()});
                for(Enchantment enc : ench) {
                    if(i > (page*10-10) && i <= (page*10)) {
                        try {
                            CItem.getLM().sendMessage(s, "cmd.enchant.body", "%id%. %name% | Min./Max.Lv.: %minlvl%/%maxlvl% | for Type: %for%", new String[] {"%id%", "%name%", "%minlvl%", "%maxlvl%", "%for%"}, new String[] {String.valueOf(i), enc.getName(), String.valueOf(enc.getStartLevel()), String.valueOf(enc.getMaxLevel()), enc.getItemTarget().name()});
                        } catch (NullPointerException ex) {
                            s.sendMessage(ChatColor.translateAlternateColorCodes('&', "Error on list " + enc.getName()));
                        }
                    }
                    i++;
                }
                
                if(maxPage > 1)
                    CItem.getLM().sendMessage(s, "cmd.enchant.footer", "%page% of %pages% Pages", new String[] {"%page%", "%pages%"}, new String[] {String.valueOf(page), String.valueOf(maxPage)});
                return true;
            case "listbooks":
                if(!s.hasPermission("citem.use.listbooks")) {
                    CItem.getLM().sendMessage(s, "cmd.listbooks.no-perm", "You have no permission to listen all books ( citem.use.books )", new String[] {"%cmd%"}, new String[] {c.getName()});
                    return true;
                }
                
                Map<String, BookMeta> books = BookData.getBooks();
                maxPage = Math.max(1,(int)Math.round(((double)books.size()/10.0d)+0.5d));
                if(page > maxPage) {
                    CItem.getLM().sendMessage(s, "cmd.listbooks.number-to-high", "The give page number is to high.", new String[] {"%cmd%"}, new String[] {c.getName()});
                    return true;
                }
                
                CItem.getLM().sendMessage(s, "cmd.listbooks.header", "The List of all available Books:", new String[] {"%cmd%"}, new String[] {c.getName()});
                for(Map.Entry<String, BookMeta> me: books.entrySet()) {
                    if(i > (page*10-10) && i <= (page*10))
                        CItem.getLM().sendMessage(s, "cmd.listbooks.body", "%id%. %name% - Written by %autor% - Title %title% with %pages% pages.", new String[] {"%id%", "%name%", "%autor%", "%title%", "%pages%", "%cmd%"}, new String[] {String.valueOf(i), me.getKey(), me.getValue().getAuthor(), me.getValue().getTitle(), String.valueOf(me.getValue().getPageCount()), c.getName()});
                    i++;
                }
                
                if(maxPage > 1)
                    CItem.getLM().sendMessage(s, "cmd.listbooks.footer", "%page% of %pages% Pages", new String[] {"%page%", "%pages%", "%cmd%"}, new String[] {String.valueOf(page), String.valueOf(maxPage), c.getName()});
                return true;
            case "savebook":
                if(!s.hasPermission("citem.use.savebook")) {
                    CItem.getLM().sendMessage(s, "cmd.savebook.no-perm", "You have not the permission to save a book ( Permission citem.use.savebook )", new String[] {"%cmd%"}, new String[] {c.getName()});
                    return true;
                }
                
                if(!(s instanceof Player)) {
                    CItem.getLM().sendMessage(s, "cmd.savebook.need-player", "This command can be only run from ingame.", new String[] {"%cmd%"}, new String[] {c.getName()});
                    return true;
                }
                
                if(other.isEmpty()) {
                    CItem.getLM().sendMessage(s, "cmd.savebook.missing-bookname", "You have forgot to give the book a name.", new String[] {"%cmd%"}, new String[] {c.getName()});
                    return true;
                }
                
                String name = other.toLowerCase();
                if(BookData.isBook(name)) {
                    CItem.getLM().sendMessage(s, "cmd.savebook.book-exist", "A book with the wished name already exist.", new String[] {"%cmd%"}, new String[] {c.getName()});
                    return true;
                }
                
                Player p = (Player)s;
                ItemStack myBook = null;
                try {
                    myBook = p.getInventory().getItemInMainHand();
                } catch(NoSuchMethodError ex) {
                    myBook = p.getItemInHand();
                }
                
                if(myBook == null || !myBook.getType().equals(Material.WRITTEN_BOOK) || !(myBook.getItemMeta() instanceof BookMeta)) {
                    CItem.getLM().sendMessage(s, "cmd.savebook.book-in-hand", "Need a book in the right Hand to save it.", new String[] {"%cmd%"}, new String[] {c.getName()});
                    return true;
                }
                
                BookMeta bm = (BookMeta)myBook.getItemMeta();
                BookData.addBook(name, bm);
                CItem.getLM().sendMessage(s, "cmd.savebook.successful", "Book was successful saved.", new String[] {"%cmd%"}, new String[] {c.getName()});
                return true;
            case "removebook":
                if(!s.hasPermission("citem.use.removebook")) {
                    CItem.getLM().sendMessage(s, "cmd.removebook.no-perm", "You need citem.use.remove permission to remove a book.", new String[] {"%cmd%"}, new String[] {c.getName()});
                    return true;
                }
                
                if(other.isEmpty()) {
                    CItem.getLM().sendMessage(s, "cmd.removebook.missing-bookname", "You have forgot to tell the name of the book.", new String[] {"%cmd%"}, new String[] {c.getName()});
                    return true;
                }
                
                String bname = other.toLowerCase();
                if(!BookData.getBooks().containsKey(bname)) {
                    CItem.getLM().sendMessage(s, "cmd.removebook.not-exist", "The book you wish to remove, not exist.", new String[] {"%cmd%"}, new String[] {c.getName()});
                    return true;
                }
                
                BookData.getBooks().remove(bname);
                CItem.getLM().sendMessage(s, "cmd.removebook.successfull", "Book was successful removed.", new String[] {"%cmd%"}, new String[] {c.getName()});
                return true;
            case "load":
                if(!s.hasPermission("citem.use.load")) {
                    CItem.getLM().sendMessage(s, "cmd.load.no-perm", "You need citem.use.load permissions to load the books.", new String[] {"%cmd%"}, new String[] {c.getName()});
                    return true;
                }
                
                BookData.load();
                CItem.getLM().sendMessage(s, "cmd.load.successfull", "Books has been successful loaded.", new String[] {"%cmd%"}, new String[] {c.getName()});
                return true;
            case "save":
                if(!s.hasPermission("citem.use.save")) {
                    CItem.getLM().sendMessage(s, "cmd.save.no-perm", "You have no permission to save the books", new String[] {"%cmd%"}, new String[] {c.getName()});
                    return true;
                }
                
                BookData.save();
                CItem.getLM().sendMessage(s, "cmd.save.successful", "Books have been saved", new String[] {"%cmd%"}, new String[] {c.getName()});
                return true;
            case "help":
                viewHelpMenu(s, uuid, a, c);
                return true;
        }
        return false;
    }

    private void viewHelpMenu(CommandSender s, UUID uuid, String[] a, Command c) {
        if(!s.hasPermission("citem.use.help")) {
            CItem.getLM().sendMessage(s, "cmd.help.no-perm", "You haven't permission to use the help.", new String[] {"%cmd%"}, new String[] {c.getName()});
            return;
        }
       
        int i = 1;
        CItem.getLM().sendMessage(s, "cmd.help.header", "Here are a list of all available commands:");
        while(!CItem.getLM().getText(uuid, "cmd.help.body." + String.valueOf(i), "--").equalsIgnoreCase("--")) {
            CItem.getLM().sendMessage(s, "cmd.help.body." + String.valueOf(i), "--", new String[] {"%cmd%"}, new String[] {c.getName()});
            i++;
        }
    }
}