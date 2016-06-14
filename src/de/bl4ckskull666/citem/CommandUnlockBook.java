/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.citem;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

/**
 *
 * @author Bl4ckSkull666
 */
public class CommandUnlockBook implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        CItem.getLM().sendMessage(s, "cmd.unlockbook.not-work-yet", "This command is under work.");
        /*if(!(s instanceof Player))
            return true;
        
        Player p = (Player)s;
        if(!p.hasPermission("citem.unlock")) {
            CItem.getLM().sendMessage(p, "no-permission-to-unlock");
            return true;
        }
        
        ItemStack inHand = null;
        try {
            inHand = p.getInventory().getItemInMainHand();
        } catch(NoSuchMethodError ex) {
            inHand= p.getItemInHand();
        }
        
        if(inHand == null || !inHand.getType().equals(Material.WRITTEN_BOOK) || !(inHand.getItemMeta() instanceof BookMeta)) {
            CItem.getLM().sendMessage(p, "need-book-in-hand");
            return true;
        }
        
        BookMeta bm = (BookMeta)inHand.getItemMeta();
        if(!bm.hasTitle()) {
            CItem.getLM().sendMessage(p, "book-is-not-looked");
            return true;
        }
        
        if(!bm.getAuthor().equalsIgnoreCase(p.getName()) && !p.hasPermission("citem.unlock.other")) {
            CItem.getLM().sendMessage(p, "book-has-other-author");
            return true;
        }
        
        ItemStack newBook = new ItemStack(Material.BOOK_AND_QUILL, inHand.getAmount());
        BookMeta bmn = (BookMeta)newBook.getItemMeta();
        bmn.setPages(bm.getPages());
        newBook.setItemMeta(bmn);
        try {
            p.getInventory().setItemInMainHand(newBook);
        } catch(NoSuchMethodError ex) {
            p.setItemInHand(newBook);
        }
        p.updateInventory();
        CItem.getLM().sendMessage(p, "reopen-book");*/
        return true;
    }
}
