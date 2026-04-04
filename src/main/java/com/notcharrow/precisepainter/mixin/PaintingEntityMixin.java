package com.notcharrow.precisepainter.mixin;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

import static com.notcharrow.precisepainter.command.PaintingCommand.playerData;

@Mixin(PaintingEntity.class)
public class PaintingEntityMixin {

	@Shadow
	@Final
	private static TrackedData<RegistryEntry<PaintingVariant>> VARIANT;

	@Inject(method = "onPlace", at = @At(value = "TAIL"))
	private void setVariant(CallbackInfo ci) {
		PaintingEntity painting = (PaintingEntity) (Object) this;
		World world = painting.getEntityWorld();
		PlayerEntity player = world.getClosestPlayer(painting, 10);
		if (player != null) {
			UUID playerUUID = player.getUuid();
			Registry<PaintingVariant> variants = world.getRegistryManager().getOrThrow(RegistryKeys.PAINTING_VARIANT);
			RegistryEntry<PaintingVariant> original = painting.getVariant();

			if (playerData.containsKey(playerUUID)) {
				RegistryKey<PaintingVariant> variantRegistryKey = playerData.get(playerUUID);
				painting.getDataTracker().set(VARIANT, variants.getEntry(variantRegistryKey.getValue()).get());
				if (!painting.canStayAttached()) {
					painting.getDataTracker().set(VARIANT, original);
					player.sendMessage(Text.literal(
							"Invalid location for: " + variantRegistryKey.getValue())
							.fillStyle(Style.EMPTY.withColor(Formatting.RED)), true);
				} else {
					playerData.remove(playerUUID);
				}
			}
		}
	}
}