/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.BookMeta
 *  org.bukkit.inventory.meta.ItemMeta
 */
package de.bl4ckskull666.citem.classes;

import de.bl4ckskull666.citem.CItem;
import de.bl4ckskull666.utils.InvSerialization.BookSerialization;
import de.bl4ckskull666.utils.InvSerialization.Util;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public final class BookData {
    
    private static final HashMap<String, BookMeta> _books = new HashMap<>();
    public static BookMeta getBook(String name) {
        if(_books.containsKey(name.toLowerCase()))
            return _books.get(name.toLowerCase());
        return null;
    }
    
    public static HashMap<String, BookMeta> getBooks() {
        return (HashMap<String, BookMeta>)_books.clone();
    }
    
    public static boolean isBook(String name) {
        return _books.containsKey(name.toLowerCase());
    }
    
    public static boolean removeBook(String name) {
        if(_books.containsKey(name.toLowerCase())) {
            _books.remove(name.toLowerCase());
            return true;
        }
        return false;
    }
    
    public static void addBook(String name, BookMeta bm) {
        _books.put(name.toLowerCase(), bm);
    }
    
    public static void load() {
        if(!CItem.getPlugin().getDataFolder().exists())
            CItem.getPlugin().getDataFolder().mkdir();

        File lFold = new File(CItem.getPlugin().getDataFolder(), "books");
        
        if(!lFold.exists())
            return;
            
        _books.clear();
        for (File l : lFold.listFiles()) {
            String name = l.getName();
            int pos = name.lastIndexOf(".");
            if(pos > 0)
                name = name.substring(0, pos);
            BookMeta bm = null;
            FileConfiguration book = YamlConfiguration.loadConfiguration(l);
            if(book.isString("book-meta")) {
                bm = BookSerialization.getBookMeta(ChatColor.translateAlternateColorCodes('&', book.getString("book-meta")));
                addBook(name, bm);
            } else {
                ItemStack item = new ItemStack(Material.WRITTEN_BOOK, 1);
                bm = (BookMeta)item.getItemMeta();
                if(book.isString("autor"))
                    bm.setAuthor(book.getString("autor"));
                if(book.isString("title"))
                    bm.setTitle(book.getString("title"));
                
                if(book.isConfigurationSection("pages")) {
                    for(String k: book.getConfigurationSection("pages").getKeys(false)) {
                        if(!Util.isNum(k))
                            continue;
                        
                        if(book.isString("pages." + k)) {
                            checkBook(bm, Integer.parseInt(k));
                            bm.setPage(Integer.parseInt(k), book.getString("pages." + k, ""));
                        } else if(book.isList("pages." + k)) {
                            String p = "";
                            for(String line: book.getStringList("pages." + k))
                                p += line + "\n";
                            
                            checkBook(bm, Integer.parseInt(k));
                            bm.setPage(Integer.parseInt(k), p);
                        }
                    }
                } else if(book.isList("pages"))
                    bm.setPages(book.getStringList("pages"));
                else
                    CItem.getPlugin().getLogger().log(Level.INFO, "Missing pages for book with name {0}", name);
            }
            
            if(bm != null)
                addBook(name, bm);
        } 
    }
    
    private static void checkBook(BookMeta bm, int page) {
        if(bm.hasPages() && bm.getPageCount() >= page)
            return;
        
        int start = 1;
        if(bm.hasPages() && bm.getPageCount() < page)
            start = bm.getPageCount();

        for(int i = start; i <= page; i++)
            bm.addPage("");
    }
    
    public static void save() {
        if(_books.isEmpty())
            return;

        File lFold = new File(CItem.getPlugin().getDataFolder(), "books");
        if(!lFold.exists())
            lFold.mkdirs();
        else if(lFold.listFiles().length > 0) {
            for(File f : lFold.listFiles())
                f.delete();
        }
        
        for(Map.Entry<String, BookMeta> me : _books.entrySet()) {
            File f = new File(lFold, me.getKey() + ".yml");
            FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
            
            fc.set("book-meta", BookSerialization.serializeBookMetaAsString(me.getValue()));
            try {
                fc.save(f);
            } catch(IOException ex) {
                CItem.getPlugin().getLogger().log(Level.WARNING, "Can''t save book with name " + me.getKey(), ex);
            }
        }
    }
}

