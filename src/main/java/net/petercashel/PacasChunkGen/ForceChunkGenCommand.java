package net.petercashel.PacasChunkGen;

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

		thread = new ChunkGenThread(worldserver, (AnvilChunkLoader)chunkloader, worldserver.theChunkProviderServer, xmin, xmax, zmin, zmax, sender);
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
		private ICommandSender sender;
		private WorldServer worldserver;
		public static boolean Running = false;

		public ChunkGenThread(WorldServer worldserver,
				AnvilChunkLoader chunkloader,
				ChunkProviderServer theChunkProviderServer, int xmin, int xmax, int zmin, int zmax, ICommandSender sender) {
			this.worldserver = worldserver;
			this.chunkloader = chunkloader;
			this.theChunkProviderServer = theChunkProviderServer;
			this.xmin = xmin;
			this.xmax = xmax;
			this.zmin = zmin;
			this.zmax = zmax;
			this.sender = sender;
		}

		public void run(){
			System.out.println("MyThread running");
			for (int x = xmin; x < xmax; x++) {
				for (int z = zmin; z < zmax; z++) {

					Runnable r = new CallbackRunnable(x, z);
					//FMLLog.warning("Generating - X" + x + " Z" + z,new Object());
					Running = true;
					net.petercashel.PacasChunkGen.ForceChunkGenCommand.ChunkGenThread.Running = true;
					ChunkIOExecutor.queueChunkLoad(worldserver, (AnvilChunkLoader)chunkloader, worldserver.theChunkProviderServer, ((int)x), ((int)z), r);
					while (Running){
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}			
			}
			FMLLog.warning("Finished Running",new Object());
			sender.addChatMessage(new ChatComponentText("Finished Running"));
		}

		public class CallbackRunnable implements Runnable {

			private int x;
			private int z;

			public CallbackRunnable(int x, int z) {
				this.x = x;
				this.z = z;
			}

			public void run(){
				FMLLog.warning("Done X" + x + " Z" + z,new Object());
				net.petercashel.PacasChunkGen.ForceChunkGenCommand.ChunkGenThread.Running = false;
				Running = false;
			}
		}
	}

}
