package com.notcharrow.precisepainter.mixin;

import com.notcharrow.precisepainter.screen.GeneratePaintingScreen;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
		if (player != null && player.getMainHandStack().getItem().equals(Items.PAINTING) && !player.isSneaking()) {
			GeneratePaintingScreen.openScreen(player, painting, VARIANT);
		}
	}
}