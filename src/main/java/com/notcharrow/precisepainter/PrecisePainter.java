package com.notcharrow.precisepainter;

import com.notcharrow.precisepainter.command.PaintingCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import static com.notcharrow.precisepainter.command.PaintingCommand.playerData;

public class PrecisePainter implements ModInitializer {

	@Override
	public void onInitialize() {
		PaintingCommand.register();

		ServerPlayConnectionEvents.DISCONNECT.register((serverPlayNetworkHandler, minecraftServer) -> {
			playerData.remove(serverPlayNetworkHandler.player.getUuid());
		});
	}
}
