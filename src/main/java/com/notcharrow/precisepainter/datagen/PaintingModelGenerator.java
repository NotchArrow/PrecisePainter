package com.notcharrow.precisepainter.datagen;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.*;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class PaintingModelGenerator extends FabricModelProvider {
	private final CompletableFuture<RegistryWrapper.WrapperLookup> registries;

	public PaintingModelGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registries) {
		super(output);
		this.registries = registries;
	}

	@Override
	public void generateItemModels(ItemModelGenerator itemModelGenerator) {
		RegistryWrapper.WrapperLookup lookup = registries.join();
		RegistryWrapper.Impl<PaintingVariant> variants = lookup.getOrThrow(RegistryKeys.PAINTING_VARIANT);

		for (RegistryEntry.Reference<PaintingVariant> variant : variants.streamEntries().toList()) {
			Identifier id = variant.getKey().get().getValue();

			Identifier rawModelId = Identifier.of("minecraft", "item/" + id.getPath());
			Models.GENERATED.upload(
					rawModelId,
					TextureMap.layer0(Identifier.of("minecraft", "painting/" + id.getPath())),
					itemModelGenerator.modelCollector
			);
		}
	}

	@Override
	public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {}
}
