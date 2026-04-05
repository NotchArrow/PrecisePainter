package com.notcharrow.precisepainter;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class PrecisePainterClient implements ClientModInitializer {
	public final String MOD_ID = "precise-painter";

	@Override
	public void onInitializeClient() {
		ResourceLoader.registerBuiltinPack(
				Identifier.of(MOD_ID, "painting-icons"),
				FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow(),
				Text.literal("Painting Icons"),
				PackActivationType.ALWAYS_ENABLED
		);
	}
}
