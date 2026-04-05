package com.notcharrow.precisepainter.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PaintingItemDefinitionsProvider implements DataProvider {
	private final FabricDataOutput output;
	private final CompletableFuture<RegistryWrapper.WrapperLookup> registries;

	public PaintingItemDefinitionsProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registries) {
		this.output = output;
		this.registries = registries;
	}

	@Override
	public CompletableFuture<?> run(DataWriter writer) {
		RegistryWrapper.WrapperLookup lookup = registries.join();
		RegistryWrapper.Impl<PaintingVariant> variants = lookup.getOrThrow(RegistryKeys.PAINTING_VARIANT);

		List<CompletableFuture<?>> futures = new ArrayList<>();

		JsonObject root = new JsonObject();
		JsonObject modelObj = new JsonObject();
		modelObj.addProperty("type", "minecraft:select");
		modelObj.addProperty("property", "minecraft:component");
		modelObj.addProperty("component", "minecraft:painting/variant");

		JsonArray cases = new JsonArray();
		for (RegistryEntry.Reference<PaintingVariant> variant : variants.streamEntries().toList()) {
			Identifier id = variant.getKey().get().getValue();
			JsonObject caseObj = new JsonObject();
			caseObj.addProperty("when", id.getPath());

			JsonObject caseModel = new JsonObject();
			caseModel.addProperty("type", "minecraft:model");
			caseModel.addProperty("model", "minecraft:item/" + id.getPath());

			caseObj.add("model", caseModel);
			cases.add(caseObj);
		}

		modelObj.add("cases", cases);

		JsonObject fallback = new JsonObject();
		fallback.addProperty("type", "minecraft:model");
		fallback.addProperty("model", "minecraft:item/painting");
		modelObj.add("fallback", fallback);

		root.add("model", modelObj);

		Path targetFile = output.getPath().resolve("assets/minecraft/items/painting.json");
		futures.add(DataProvider.writeToPath(writer, root, targetFile));

		return DataProvider.writeToPath(writer, root, targetFile);
	}

	@Override
	public String getName() {
		return "Painting Item Definitions";
	}
}