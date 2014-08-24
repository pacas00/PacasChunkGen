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

public class CancelChunkGenCommand extends CommandBase {

	private PacasChunkGen PacasChunkGen;

	public CancelChunkGenCommand(PacasChunkGen PacasChunkGen) {
		super();
		this.PacasChunkGen = PacasChunkGen;
	}

	@Override
	public String getCommandName() {
		return "CancelChunkGen";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "commands.CancelChunkGen.usage";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		ForceChunkGenCommand.ChunkGenThread.ABORT = true;
	}
}
