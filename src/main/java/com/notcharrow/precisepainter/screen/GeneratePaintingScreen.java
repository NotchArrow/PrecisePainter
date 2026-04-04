package com.notcharrow.precisepainter.screen;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class GeneratePaintingScreen {
	public static void openScreen(PlayerEntity player, PaintingEntity paintingEntity, TrackedData<RegistryEntry<PaintingVariant>> VARIANT) {
		SimpleInventory customInv = new SimpleInventory(54);

		World world = paintingEntity.getEntityWorld();
		Registry<PaintingVariant> variants = world.getRegistryManager().getOrThrow(RegistryKeys.PAINTING_VARIANT);
		RegistryEntry<PaintingVariant> original = paintingEntity.getDataTracker().get(VARIANT);

		int i = 0;
		for (Identifier identifier : variants.getIds().stream().sorted().toList()) {
			paintingEntity.getDataTracker().set(VARIANT, variants.getEntry(identifier).orElseThrow());
			if (paintingEntity.canStayAttached()) {
				ItemStack itemStack = new ItemStack(Items.PAINTING);
				itemStack.set(DataComponentTypes.PAINTING_VARIANT, variants.getEntry(identifier).orElseThrow());
				customInv.setStack(i, itemStack);
				i++;
			}
		}
		paintingEntity.getDataTracker().set(VARIANT, original);

		player.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, inv, p) ->
				new PaintingScreenHandler(syncId, inv, customInv, paintingEntity, VARIANT),
				Text.literal("Painting Selection Menu")));
	}
}
