/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.citem.classes;

import de.bl4ckskull666.citem.CItem;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.mu1ti1ingu41.Mu1ti1ingu41;
import de.bl4ckskull666.mu1ti1ingu41.utils.Utils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 *
 * @author Bl4ckSkull666
 */
public final class Lang {
    private boolean _isMu1ti1ingu41 = false;
    private boolean _isSpigot  = false;
    private FileConfiguration _fc = null;  
    public Lang() {
        if(Bukkit.getPluginManager().isPluginEnabled("Mu1ti1ingu41")) {
            Mu1ti1ingu41.loadExternalDefaultLanguage(CItem.getPlugin(), "languages");
            _isMu1ti1ingu41 = true;
        } else {
            _isSpigot = Bukkit.getVersion().toLowerCase().contains("spigot");
            File f = new File(CItem.getPlugin().getDataFolder(), "language.yml");
            if(!f.exists())
                loadLanguageFile();
            else
                _fc = YamlConfiguration.loadConfiguration(f);
        }
    }
    
    public void sendMessage(CommandSender cs, String path, String def) {
        sendMessage(cs, path, def, new String[] {}, new String[] {});
    }
    
    public void sendMessage(CommandSender cs, String path, String def, String[] search, String[] replace) {
        if(cs instanceof Player)
            sendMessage((Player)cs, path, def, search, replace);
        else
            cs.sendMessage(getText(UUID.fromString("00000000-0000-0000-0000-000000000000"), path, def, search, replace));
    }
    
    public void sendMessage(Player p, String path, String def) {
        sendMessage(p, path, def, new String[] {}, new String[] {});
    }
    
    public void sendMessage(Player p, String path, String def, String[] search, String[] replace) {
        if(_isMu1ti1ingu41) {
            Language.sendMessage(CItem.getPlugin(), p, path, def, search, replace);
            return;
        }
        
        if(!_fc.isString(path) && !_fc.isConfigurationSection(path)) {
            p.sendMessage(replaceAll(def, search, replace));
            return;
        }
        
        if(_isSpigot) {
            if(_fc.isConfigurationSection(path) && _fc.isString(path + ".message")) {
                TextComponent msg = new TextComponent(replaceAll(_fc.getString(path + ".message"), search, replace));
                if(_fc.isString(path + ".hover-msg")) {
                    msg.setHoverEvent(
                        new HoverEvent(
                            Utils.isHoverAction("show_" + _fc.getString(path + ".hover-type", "text"))?HoverEvent.Action.valueOf(("show_" + _fc.getString(path + ".hover-type", "text")).toUpperCase()):HoverEvent.Action.SHOW_TEXT, 
                            new ComponentBuilder(replaceAll(_fc.getString(path + ".hover-msg"), search, replace)).create()
                        )
                    );
                }
                if(_fc.isString(path + ".click-msg")) {
                    msg.setClickEvent(
                        new ClickEvent(
                            Utils.isClickAction(_fc.getString(path + ".click-type", "open_url"))?ClickEvent.Action.valueOf(_fc.getString(path + ".click-type", "open_url").toUpperCase()):ClickEvent.Action.OPEN_URL, 
                            replaceAll(_fc.getString(path + ".click-msg"), search, replace)
                        )
                    );
                }
                p.spigot().sendMessage(msg);
                return;
            }
        }
        p.sendMessage(replaceAll(_fc.getString(path), search, replace));
    }
    
    public String getText(UUID uuid, String path, String def) {
        return getText(uuid, path, def, new String[] {}, new String[] {});
    }
    
    public String getText(UUID uuid, String path, String def, String[] search, String[] replace) {
        if(_isMu1ti1ingu41)
            return Language.getText(CItem.getPlugin(), uuid, path, def, search, replace);
        
        if(!_fc.isString(path) && !_fc.isString(path + ".message"))
            return replaceAll(def, search, replace);
        
        if(_fc.isString(path + ".message"))
            return replaceAll(_fc.getString(path + ".message"), search, replace);
        else
            return replaceAll(_fc.getString(path), search, replace);
    }
    
    public FileConfiguration getFile(UUID uuid) {
        if(_isMu1ti1ingu41)
            return Language.getMessageFile(CItem.getPlugin(), uuid);
        return _fc;
    }
    
    private void loadLanguageFile() {
        InputStream in = CItem.getPlugin().getResource("languages/lang.yml");
        int c = -1;
        File spLang = new File(CItem.getPlugin().getDataFolder(), "language.yml");
        try {
            OutputStream os = new FileOutputStream(spLang);
            while((c = in.read()) != -1)
                os.write(c);
            os.close();
            in.close();
            _fc = YamlConfiguration.loadConfiguration(spLang);
            _fc.save(spLang);
        } catch (IOException ex) {
            
        }
    }
    
    private String replaceAll(String msg, String[] search, String[] replace) {
        if(search.length > 0 && replace.length > 0 && search.length == replace.length) {
            for(int i = 0; i < search.length; i++)
                msg = msg.replace(search[i], replace[i]);
        }
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}

