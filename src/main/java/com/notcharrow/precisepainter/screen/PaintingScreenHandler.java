package com.notcharrow.precisepainter.screen;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class PaintingScreenHandler extends GenericContainerScreenHandler {

	private final PaintingEntity paintingEntity;
	private final TrackedData<RegistryEntry<PaintingVariant>> VARIANT;

	public PaintingScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PaintingEntity paintingEntity,
								 TrackedData<RegistryEntry<PaintingVariant>> VARIANT) {
		super(ScreenHandlerType.GENERIC_9X6, syncId, playerInventory, inventory, 6);
		this.paintingEntity = paintingEntity;
		this.VARIANT = VARIANT;
	}

	@Override
	public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
		if (slotIndex >= 0 && slotIndex < 54) {
			ItemStack clickedStack = this.getInventory().getStack(slotIndex);

			if (clickedStack.isOf(Items.PAINTING)) {
				RegistryEntry<PaintingVariant> variant = clickedStack.get(DataComponentTypes.PAINTING_VARIANT);
				RegistryEntry<PaintingVariant> original = this.paintingEntity.getDataTracker().get(VARIANT);
				this.paintingEntity.getDataTracker().set(VARIANT, variant);

				if (!this.paintingEntity.canStayAttached()) {
					this.paintingEntity.getDataTracker().set(VARIANT, original);
					player.sendMessage(Text.of("Invalid Size!"), true);
				} else {
					if (player instanceof ServerPlayerEntity serverPlayer) {
						serverPlayer.closeHandledScreen();
					} else if (player instanceof ClientPlayerEntity clientPlayer) {
						clientPlayer.closeHandledScreen();
					}
				}
			}

			this.syncState();
			return;
		}
		super.onSlotClick(slotIndex, button, actionType, player);
	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return true;
	}
}
