package net.petercashel.PacasChunkGen;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;

@MCVersion(value = "1.7.10")
public class PacasChunkGenFMLLoadingPlugin implements cpw.mods.fml.relauncher.IFMLLoadingPlugin {

	@Override
	public String[] getASMTransformerClass() {
		return new String[]{PacasChunkGenClassTransformer.class.getName()};
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		
	}

	@Override
	public String getAccessTransformerClass() {
		// TODO Auto-generated method stub
		return null;
	}

}
