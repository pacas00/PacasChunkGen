package net.petercashel.PacasChunkGen;


import java.util.List;

import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(modid = "PacasChunkGen", name = "PacasChunkGen", version = "1.0")
public class PacasChunkGen implements LoadingCallback{

	@Instance(value = "PacasChunkGen")
	public static PacasChunkGen instance;

	private MinecraftServer server;
	private ForceChunkGenCommand ForceChunkGenCMD;

	public static boolean doNextLoad;


	@EventHandler
	public void init(FMLInitializationEvent event){
		ForgeChunkManager.setForcedChunkLoadingCallback(this.instance, this);	
	}

	@EventHandler
	public void ServerStarting(FMLServerStartingEvent event) 
	{
		server = MinecraftServer.getServer();
		ServerCommandManager commands = (ServerCommandManager) server.getCommandManager();
		ForceChunkGenCMD = new ForceChunkGenCommand(this);
		commands.registerCommand(ForceChunkGenCMD);
	}

	@Override
	public void ticketsLoaded(List<Ticket> tickets, World world) {
		// TODO Auto-generated method stub
		// Because i dont need the tickets after use. ill unload them here if they still exist.

		for (int i = 0; i < tickets.size(); i++) {
			Ticket ticket = tickets.get(i);
			if(ticket != null) {
				ForgeChunkManager.releaseTicket(ticket);
			}
		}
	}
}
