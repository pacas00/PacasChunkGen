package net.petercashel.PacasChunkGen;

import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import cpw.mods.fml.relauncher.FMLRelaunchLog;


public class PacasChunkGenClassTransformer implements net.minecraft.launchwrapper.IClassTransformer {
	
	private static boolean debug = true;

	@Override
	public byte[] transform(String arg0, String arg1, byte[] arg2) {


		if (arg0.equals("bjd")) {
			if (debug) System.out.println("*********PacasChunkGen INSIDE OBFUSCATED ChunkProviderClient TRANSFORMER ABOUT TO PATCH: " + arg0);
			return patchClassASM(arg0, arg2, true);
		}
		if (arg0.equals("net.minecraft.client.multiplayer.ChunkProviderClient")) {
			if (debug) System.out.println("*********PacasChunkGen INSIDE ChunkProviderClient TRANSFORMER ABOUT TO HACK: " + arg0);
			return patchClassASM(arg0, arg2, false);
		}




		return arg2;
	}
	
	public byte[] patchClassASM(String name, byte[] bytes, boolean obfuscated) {

		String targetMethodName = "";
		
		if(obfuscated == true) {
			targetMethodName ="d";
		} else {
			targetMethodName ="unloadQueuedChunks";
		}

		//set up ASM class manipulation stuff. Consult the ASM docs for details
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);



		//Now we loop over all of the methods declared inside the Explosion class until we get to the targetMethodName "doExplosionB"

		// find method to inject into
		Iterator<MethodNode> methods = classNode.methods.iterator();
		while(methods.hasNext())
		{
			MethodNode m = methods.next();
			if (debug) System.out.println("*********PacasChunkGen Method Name: "+m.name + " Desc:" + m.desc);

			if (m.name.equals(targetMethodName) && m.desc.equals("()Z")) {
				if (debug) System.out.println("*********PacasChunkGen Inside target method!");

				InsnList toInject = new InsnList();

				toInject.add(new InsnNode(Opcodes.ICONST_0));
				toInject.add(new InsnNode(Opcodes.IRETURN));

				m.instructions.clear();
				m.instructions.add(toInject);


				if (debug) System.out.println("unloadQueuedChunks Hacking Complete!");
			}
		}

		//ASM specific for cleaning up and returning the final bytes for JVM processing.
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		return writer.toByteArray();
	}
}
