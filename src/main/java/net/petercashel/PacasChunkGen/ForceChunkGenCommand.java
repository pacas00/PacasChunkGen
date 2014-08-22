package net.petercashel.PacasChunkGen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.chunkio.ChunkIOExecutor;

public class ForceChunkGenCommand extends CommandBase {

	private PacasChunkGen PacasChunkGen;
	public static ChunkGenThread thread;

	public ForceChunkGenCommand(PacasChunkGen PacasChunkGen) {
		super();
		this.PacasChunkGen = PacasChunkGen;
	}

	@Override
	public String getCommandName() {
		return "ForceChunkGen";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender)
	{
		return "commands.ForceChunkGen.usage";
	}

	@Override
	public int getRequiredPermissionLevel()
	{
		return 2;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {

		MinecraftServer server = MinecraftServer.getServer();
		WorldServer worldserver = server.worldServerForDimension(0);
		IChunkLoader chunkloader = worldserver.theChunkProviderServer.currentChunkLoader;

		try {
			if (args[0] == null || args[1] == null || args[2] == null || args[3] == null) {
				sender.addChatMessage(new ChatComponentText("ForceChunkGen X_Min X_Max Z_Min Z_Max"));
				return;
			}
		} catch (NullPointerException e) {
			sender.addChatMessage(new ChatComponentText("ForceChunkGen X_Min X_Max Z_Min Z_Max"));
			return;
		}

		int xmin = Integer.parseInt(args[0])/ 16;
		int xmax = Integer.parseInt(args[1])/ 16;
		int zmin = Integer.parseInt(args[2])/ 16;
		int zmax = Integer.parseInt(args[3])/ 16;

		FMLLog.bigWarning("WARNING! GENERATING CHUNKS! THIS MAY TAKE A LONG TIME AND WILL LOG EVERYTHING!",new Object());
		thread = new ChunkGenThread(worldserver, (AnvilChunkLoader)chunkloader, worldserver.theChunkProviderServer, xmin, xmax, zmin, zmax);
		thread.start();



	}

	public ModContainer findContainerFor(Object mod)
	{
		if( mod instanceof ModContainer )
		{
			return (ModContainer)mod;
		}

		return Loader.instance().getReversedModObjectList().get(mod);
	}

	public static class ChunkGenThread extends Thread {

		private AnvilChunkLoader chunkloader;
		private ChunkProviderServer theChunkProviderServer;
		private int xmin;
		private int xmax;
		private int zmin;
		private int zmax;
		private WorldServer worldserver;
		public static boolean Running = false;

		public ChunkGenThread(WorldServer worldserver,
				AnvilChunkLoader chunkloader,
				ChunkProviderServer theChunkProviderServer, int xmin, int xmax, int zmin, int zmax) {
			this.worldserver = worldserver;
			this.chunkloader = chunkloader;
			this.theChunkProviderServer = theChunkProviderServer;
			this.xmin = xmin;
			this.xmax = xmax;
			this.zmin = zmin;
			this.zmax = zmax;
		}

		public void run(){
			ArrayList<ChunkPair> failed = new ArrayList<ChunkPair>();
			for (int x = xmin; x < xmax; x++) {
				for (int z = zmin; z < zmax; z++) {
					if (!chunkloader.chunkExists(worldserver, x, z)) {
						Runnable r = new CallbackRunnable(x, z);
						Running = true;
						net.petercashel.PacasChunkGen.ForceChunkGenCommand.ChunkGenThread.Running = true;
						ChunkIOExecutor.queueChunkLoad(worldserver, (AnvilChunkLoader)chunkloader, worldserver.theChunkProviderServer, ((int)x), ((int)z), r);
						int count = 0;
						while (Running){
							try {
								Thread.sleep(100);
								count++;
								if (count > 50) {
									ChunkPair pair = new ChunkPair(x,z);
									failed.add(pair);
									Running = false;
								}
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					} else {
						FMLLog.info("Skipping X" + x + " Z" + z,new Object());
					}
				}		
			}
			if (failed.size() > 0) {
				Iterator itr = failed.iterator();
				while (itr.hasNext()) {
					ChunkPair p = (ChunkPair) itr.next();
					int x = p.x;
					int z = p.z;
					Runnable r = new CallbackRunnable(x, z);
					Running = true;
					net.petercashel.PacasChunkGen.ForceChunkGenCommand.ChunkGenThread.Running = true;
					ChunkIOExecutor.queueChunkLoad(worldserver, (AnvilChunkLoader)chunkloader, worldserver.theChunkProviderServer, ((int)x), ((int)z), r);
					int count = 0;
					while (Running){
						try {
							Thread.sleep(100);
							count++;
							if (count > 80) {
								FMLLog.info("Chunk X" + x + " Z" + z + " Failed on second attempt!",new Object());
								Running = false;
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
				}
			}
			FMLLog.info("Finished Running",new Object());
		}

		public class CallbackRunnable implements Runnable {

			private int x;
			private int z;

			public CallbackRunnable(int x, int z) {
				this.x = x;
				this.z = z;
			}

			public void run(){
				FMLLog.info("Done X" + x + " Z" + z,new Object());
				net.petercashel.PacasChunkGen.ForceChunkGenCommand.ChunkGenThread.Running = false;
				Running = false;
			}
		}
		
		public class ChunkPair {
			  int x;
			  int z;
			  ChunkPair(int x, int z) {this.x=x;this.z=z;}
			}
	}
	
	

}
