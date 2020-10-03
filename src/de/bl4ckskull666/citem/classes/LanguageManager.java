package de.bl4ckskull666.citem.classes;

import de.bl4ckskull666.citem.CItem;
import static de.bl4ckskull666.citem.utils.Util.UpperFirst;
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
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Bl4ckSkull666
 */
public final class LanguageManager {
    private boolean _isSpigot  = false;
    private FileConfiguration _fc = null;  
    public LanguageManager() {
        _isSpigot = Bukkit.getVersion().toLowerCase().contains("spigot");
        File f = new File(CItem.getPlugin().getDataFolder(), "language.yml");
        if(!f.exists())
            loadLanguageFile();
        else
            _fc = YamlConfiguration.loadConfiguration(f);
        
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
    
    public String getItemName(UUID uuid, ItemStack item) {
        return getText(uuid, "item-name." + item.getType().name().toLowerCase(), UpperFirst(item.getType().name().replace("_", " ")));
    }
    
    public String getText(UUID uuid, String path, String def) {
        return getText(uuid, path, def, new String[] {}, new String[] {});
    }
    
    public String getText(UUID uuid, String path, String def, String[] search, String[] replace) {
        if(!_fc.isString(path) && !_fc.isString(path + ".message"))
            return replaceAll(def, search, replace);
        
        if(_fc.isString(path + ".message"))
            return replaceAll(_fc.getString(path + ".message"), search, replace);
        else
            return replaceAll(_fc.getString(path), search, replace);
    }
    
    public FileConfiguration getFile(UUID uuid) {
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
/*
import de.bl4ckskull666.citem.CItem;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.mu1ti1ingu41.Mu1ti1ingu41;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class LanguageManager {
    private boolean _useMu1ti1ingu41 = Bukkit.getPluginManager().isPluginEnabled("Mu1ti1ingu41");
    private FileConfiguration _fc = null;
    private File _f = null;

    public LanguageManager() {
        if(_useMu1ti1ingu41) {
            if(CItem.getBoolean("use-plugin.mu1ti1ingu41", false)) {
                Mu1ti1ingu41.loadExternalDefaultLanguage((Plugin)CItem.getPlugin(), (String)"languages");
            } else {
                _useMu1ti1ingu41 = false;
            }
        }
        load();
    }

    public void load() {
        _f = new File(CItem.getPlugin().getDataFolder(), "language.yml");
        _fc = YamlConfiguration.loadConfiguration((File)this._f);
        if (!_f.exists()) {
            try {
                InputStream in = CItem.getPlugin().getResource("languages/" + CItem.getString("language.default", "en") + ".yml");
                String msg = "";
                int c = -1;
                while((c = in.read()) != -1)
                    msg = msg + String.valueOf(c);
                _fc.loadFromString(msg);
                _fc.save(_f);
            } catch (IOException | InvalidConfigurationException ex) {
                CItem.getPlugin().getLogger().log(Level.WARNING, "Error on load default configuration file.");
            }
        }
    }

    public void sendMessage(Player p, String path) {
        if (!this._fc.isString(path)) {
            this.saveMissingMessage(path);
        }
        if (this._useMu1ti1ingu41) {
            Language.sendMessage(CItem.getPlugin(), p, path, _fc.getString(path));
        } else {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', _fc.getString(path)));
        }
    }

    public void sendMessage(Player p, String path, String[] search, String[] replace) {
        if(!_fc.isString(path))
            saveMissingMessage(path);

        if(_useMu1ti1ingu41)
            Language.sendMessage(CItem.getPlugin(), p, path, _fc.getString(path), search, replace);
        else {
            String msg = this._fc.getString(path);
            if(search.length == replace.length) {
                for(int i = 0; i < search.length; ++i)
                    msg = msg.replace(search[i], replace[i]);
            }
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
        }
    }

    public String getMessage(UUID uuid, String path) {
        if(uuid == null)
            uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        
        if(!_fc.isString(path))
            saveMissingMessage(path);
        
        if(_useMu1ti1ingu41)
            return Language.getMessage(CItem.getPlugin(), uuid, path, _fc.getString(path));

        return ChatColor.translateAlternateColorCodes('&', _fc.getString(path));
    }

    public String getMessage(UUID uuid, String path, String[] search, String[] replace) {
        if (uuid == null) {
            uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        }
        if (!this._fc.isString(path)) {
            this.saveMissingMessage(path);
        }
        if (this._useMu1ti1ingu41) {
            return Language.getMessage((Plugin)CItem.getPlugin(), (UUID)uuid, (String)path, (String)this._fc.getString(path), (String[])search, (String[])replace);
        }
        String msg = this._fc.getString(path);
        if(search.length == replace.length) {
            for (int i = 0; i < search.length; ++i) {
                msg = msg.replace(search[i], replace[i]);
            }
        }
        return ChatColor.translateAlternateColorCodes((char)'&', (String)msg);
    }

    public boolean isString(UUID uuid, String path) {
        if (uuid == null) {
            uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        }
        if (this._useMu1ti1ingu41) {
            FileConfiguration fc = Language.getMessageFile((Plugin)CItem.getPlugin(), (UUID)uuid);
            return fc.isString(path);
        }
        return this._fc.isString(path);
    }

    private void saveMissingMessage(String path) {
        _fc.set(path, (Object)"&cMissing Message");
        try {
            _fc.save(this._f);
        } catch (IOException ex) {
            CItem.getPlugin().getLogger().log(Level.WARNING, "Error on save missing language message.");
        }
    }
}*/

