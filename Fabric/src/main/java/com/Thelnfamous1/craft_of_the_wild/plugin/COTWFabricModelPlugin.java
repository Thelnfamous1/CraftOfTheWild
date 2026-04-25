package com.Thelnfamous1.craft_of_the_wild.plugin;

import com.Thelnfamous1.craft_of_the_wild.Constants;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class COTWFabricModelPlugin implements PreparableModelLoadingPlugin<Map<ResourceLocation, SeparateTransformsData>> {

    public static final DataLoader<Map<ResourceLocation, SeparateTransformsData>> LOADER = (resourceManager, executor) -> {
        return CompletableFuture.supplyAsync(() -> {
            Map<ResourceLocation, SeparateTransformsData> result = new HashMap<>();

            var resources = resourceManager.listResources(
                    "models/item",
                    path -> path.getPath().endsWith(".json")
            );

            for (var entry : resources.entrySet()) {

                ResourceLocation fileId = entry.getKey();

                try (var reader = entry.getValue().openAsReader()) {

                    JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

                    if (!json.has("loader")) continue;

                    if (!"forge:separate_transforms"
                            .equals(GsonHelper.getAsString(json, "loader"))) {
                        continue;
                    }

                    // BASE MODEL (parent reference)
                    ResourceLocation base = new ResourceLocation(
                            GsonHelper.getAsString(GsonHelper.getAsJsonObject(json, "base"), "parent")
                    );

                    // PERSPECTIVE MODELS (all are just model locations)
                    Map<ItemDisplayContext, ResourceLocation> perspectives = new HashMap<>();

                    if (json.has("perspectives")) {

                        JsonObject pers = json.getAsJsonObject("perspectives");

                        for (String key : pers.keySet()) {

                            ItemDisplayContext ctx = parseContext(key);
                            if(ctx == null) continue;

                            JsonElement element = pers.get(key);

                            if (!element.isJsonObject()) continue;

                            JsonObject obj = element.getAsJsonObject();

                            if (!obj.has("parent")) continue;

                            ResourceLocation model = new ResourceLocation(
                                    obj.get("parent").getAsString()
                            );

                            perspectives.put(ctx, model);
                        }
                    }

                    result.put(
                            toModelId(fileId),
                            new SeparateTransformsData(base, perspectives)
                    );

                } catch (Exception e) {
                    Constants.LOG.error(
                            "Failed loading separate_transforms model: {}",
                            fileId,
                            e
                    );
                }
            }

            return result;

        }, executor);
    };

    @Nullable
    private static ItemDisplayContext parseContext(String key){
        for (ItemDisplayContext transform : ItemDisplayContext.values()) {
            if (key.equals(transform.getSerializedName())) {
                return transform;
            }
        }
        return null;
    }

    private static ResourceLocation toModelId(ResourceLocation fileId) {
        String path = fileId.getPath()
                .replace("models/item/", "")
                .replace(".json", "");

        return new ResourceLocation(fileId.getNamespace(), path);
    }

    @Override
    public void onInitializeModelLoader(Map<ResourceLocation, SeparateTransformsData> data, ModelLoadingPlugin.Context context) {
        for (Map.Entry<ResourceLocation, SeparateTransformsData> dataEntry : data.entrySet()) {
            SeparateTransformsData model = dataEntry.getValue();
            Set<ResourceLocation> modelSet = new HashSet<>();
            modelSet.add(model.baseModel());
            modelSet.addAll(model.perspectives().values());
            context.addModels(modelSet);
            Constants.LOG.info("Adding models {} to context for {}", modelSet, dataEntry.getKey());
        }

        context.modifyModelAfterBake().register((originalModel, ctx) -> {

            ResourceLocation id = new ResourceLocation(ctx.id().getNamespace(), ctx.id().getPath());

            SeparateTransformsData entry = data.get(id);

            if (entry == null) return originalModel;

            ModelBaker baker = ctx.baker();

            BakedModel base = baker.bake(entry.baseModel(), BlockModelRotation.X0_Y0);

            Map<ItemDisplayContext, BakedModel> perspectives = new HashMap<>();

            for (var e : entry.perspectives().entrySet()) {
                perspectives.put(
                        e.getKey(),
                        baker.bake(e.getValue(), BlockModelRotation.X0_Y0)
                );
            }

            return new FabricSeparateTransformsModel(originalModel, base, perspectives);
        });
    }
}