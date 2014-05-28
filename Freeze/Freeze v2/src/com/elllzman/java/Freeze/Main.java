package com.elllzman.java.Freeze;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;



import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;


public class Main extends JavaPlugin implements Listener {

	
	boolean FreezetoggleGlobal = false; 
	private ArrayList<Player> FrozenPlayers = new ArrayList<Player>();
	
	
	public void onEnable()
	{
		Logger.getLogger("Freeze has been invoked!");
		getServer().getPluginManager().registerEvents(this, this);
		initialiseConfig();
		

	}
	
	private void initialiseConfig() {
		
		FileConfiguration config = getConfig();

		config.options().copyDefaults(true);
		saveConfig();
		
	}
	
	private boolean OpCheck(Player P)
	{
		if(P.isOp()==true&&getConfig().getBoolean("Freeze OP's")==false)
		{
			return true;
		}
		else;
			return false;
	}
	
	@EventHandler
	public boolean onDamage(EntityDamageEvent event)
	{
		if(FreezetoggleGlobal==true)
		{
			if(event.getEntityType()==org.bukkit.entity.EntityType.PLAYER)
			{
				event.setCancelled(true);
			}
		}
		else if(FrozenPlayers.contains(event.getEntity().getPassenger()))
		{
			event.setCancelled(true);
		}
		return false;
	}
	
	@EventHandler
	public boolean onMove(PlayerMoveEvent event)
	{
		if(getConfig().getBoolean("Bukkit Freeze Enabled"))
		{
			Player P = event.getPlayer();
			if(FreezetoggleGlobal==true&&OpCheck(P) == false)
			{	
				if((( event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN, 1)).isEmpty() == true)) 
				{
					event.setTo(event.getFrom().add(new Vector(0, -0.5D, 0)));
				}
				Location old = event.getFrom();
				event.getPlayer().teleport(old);
			}
			else if(FrozenPlayers.contains(event.getPlayer())&&OpCheck(P)==false)
			{
				if((( event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN, 1)).isEmpty() == true)) 
				{
					event.setTo(event.getFrom().add(new Vector(0, -0.5D, 0)));
				}
				Location old = event.getFrom();
				event.getPlayer().teleport(old);
			}
		}
		return false;
		
	}
	
	@EventHandler
	public void blockBreak(BlockBreakEvent event)
	{
		if(FreezetoggleGlobal==true)
		{
			event.setCancelled(true);
		}
		else if(FrozenPlayers.contains(event.getPlayer()))
		{
			event.setCancelled(true);
		}
			
	}
	
	@EventHandler
	public void onHungerChange(FoodLevelChangeEvent event)
	{
		if(FreezetoggleGlobal==true)
		{
			Entity P = event.getEntity();
			((Player) P).setFoodLevel(10);
		}
		else if(FrozenPlayers.contains(event.getEntity().getPassenger()))
		{
			Entity P = event.getEntity();
			((Player) P).setFoodLevel(10);
		}
	}

	

	public boolean onCommand(CommandSender sender,Command cmd, String label, String[] args)
	{
		if(sender instanceof Player)
		{
			
			if(cmd.getName().equalsIgnoreCase("Fr"))
			{
				if(args.length==0||args[0].equalsIgnoreCase("*")==true)
				{
					if(FreezetoggleGlobal==true)
					{
						FreezetoggleGlobal=false;
						
						if(getConfig().getString("Unfreeze message")!=null)
						{
							Bukkit.getServer().broadcastMessage(ChatColor.GOLD + getConfig().getString("Unfreeze message"));
						}
						Player[] Players = Bukkit.getServer().getOnlinePlayers();
						List<Player> PlayerList = Arrays.asList(Players);
						Iterator<Player> Loop = PlayerList.iterator();						
						while (Loop.hasNext()){
							Player TempHolder = Loop.next();
							TempHolder.removePotionEffect(PotionEffectType.BLINDNESS);
							TempHolder.removePotionEffect(PotionEffectType.SLOW);
						}
						
						
						
						return true;
					}
					else if(FreezetoggleGlobal==false)
					{
						FreezetoggleGlobal=true;
						if(getConfig().getString("Freeze message")!=null)
						{
					
							Bukkit.getServer().broadcastMessage(ChatColor.GOLD + getConfig().getString("Freeze message"));
						}
						

						Player P = (Player) sender;
						Player[] Players = Bukkit.getServer().getOnlinePlayers();
						List<Player> PlayerList = Arrays.asList(Players);
						Iterator<Player> Loop = PlayerList.iterator();

						
						while (Loop.hasNext()){
							Player TempHolder = Loop.next();
							TempHolder.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 500000000, 5));
							if(ApplyBlindness()==true||OpCheck(P)==false){
								TempHolder.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 500000000, 1));
							}
						}
						
						return true;
					}
					return false;
				}
				if(args.length==1)
				{
					Player P = Bukkit.getPlayer(args[0]);
					
						if(P==null)
						{
							sender.sendMessage(ChatColor.GOLD + "That player is not online!");
							return false;
						}

						if(OpCheck(P)==false)
						{
							sender.sendMessage("That player is OP and Freezing OP's is not enabled!");
							return true;
						}
				
						
						else if(FrozenPlayers.contains(P))
						{
							FrozenPlayers.remove(P);
							
							if(getConfig().getString("PlayerUnfreeze message")!=null)
							{
								sender.sendMessage(ChatColor.GOLD + P.getName() + getConfig().getString("PlayerUnfreeze message"));
							}
							P.removePotionEffect(PotionEffectType.BLINDNESS);
							P.removePotionEffect(PotionEffectType.SLOW);
							P.sendMessage(ChatColor.GOLD + "You were unfrozen by " + sender.getName());
							return true;
						}
						else if(FrozenPlayers.contains(P)==false)
						{

							FrozenPlayers.add(P);
							
							if(getConfig().getString("PlayerFreeze message")!=null)
							{
								sender.sendMessage(ChatColor.GOLD + P.getName() + getConfig().getString("PlayerFreeze message"));
							}
							if(ApplyBlindness()==true||OpCheck(P)==false){
								P.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 500000000, 1));
								P.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 500000000, 5));
								
							}
							P.sendMessage(ChatColor.GOLD + "You were frozen by " + sender.getName());
							return true;
						}
					}
				}
					
		}	
		return false;
	}






	private boolean ApplyBlindness() {
		if(getConfig().getBoolean("Apply Blindness to players")==true)
		{
			return true;
		}
		else;
			return false;
	}
	
}