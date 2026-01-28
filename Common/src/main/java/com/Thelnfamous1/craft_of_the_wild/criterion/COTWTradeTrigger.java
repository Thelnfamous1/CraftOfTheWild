package com.Thelnfamous1.craft_of_the_wild.criterion;

import com.Thelnfamous1.craft_of_the_wild.entity.COTWVillager;
import com.Thelnfamous1.craft_of_the_wild.init.CriterionInit;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public class COTWTradeTrigger extends SimpleCriterionTrigger<COTWTradeTrigger.TriggerInstance> {
   final ResourceLocation id;

   public COTWTradeTrigger(ResourceLocation pId) {
      this.id = pId;
   }

   @Override
   public ResourceLocation getId() {
      return this.id;
   }

   @Override
   public COTWTradeTrigger.TriggerInstance createInstance(JsonObject pJson, ContextAwarePredicate pPredicate, DeserializationContext pDeserializationContext) {
      ContextAwarePredicate contextawarepredicate = EntityPredicate.fromJson(pJson, "villager", pDeserializationContext);
      ItemPredicate itempredicate = ItemPredicate.fromJson(pJson.get("item"));
      return new COTWTradeTrigger.TriggerInstance(this.id, pPredicate, contextawarepredicate, itempredicate);
   }

   public <T extends Entity & COTWVillager> void trigger(ServerPlayer pPlayer, T pVillager, ItemStack pStack) {
      LootContext lootcontext = EntityPredicate.createContext(pPlayer, pVillager);
      this.trigger(pPlayer, (triggerInstance) -> triggerInstance.matches(lootcontext, pStack));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ContextAwarePredicate villager;
      private final ItemPredicate item;

      public TriggerInstance(ResourceLocation location, ContextAwarePredicate pPlayer, ContextAwarePredicate pVillager, ItemPredicate pItem) {
         super(location, pPlayer);
         this.villager = pVillager;
         this.item = pItem;
      }

      public static COTWTradeTrigger.TriggerInstance tradedWithVillager() {
         return new COTWTradeTrigger.TriggerInstance(CriterionInit.TRADE.id, ContextAwarePredicate.ANY, ContextAwarePredicate.ANY, ItemPredicate.ANY);
      }

      public static COTWTradeTrigger.TriggerInstance tradedWithVillager(EntityPredicate.Builder pVillager) {
         return new COTWTradeTrigger.TriggerInstance(CriterionInit.TRADE.id, EntityPredicate.wrap(pVillager.build()), ContextAwarePredicate.ANY, ItemPredicate.ANY);
      }

      public boolean matches(LootContext pContext, ItemStack pStack) {
         if (!this.villager.matches(pContext)) {
            return false;
         } else {
            return this.item.matches(pStack);
         }
      }

      @Override
      public JsonObject serializeToJson(SerializationContext pConditions) {
         JsonObject jsonobject = super.serializeToJson(pConditions);
         jsonobject.add("item", this.item.serializeToJson());
         jsonobject.add("villager", this.villager.toJson(pConditions));
         return jsonobject;
      }
   }
}