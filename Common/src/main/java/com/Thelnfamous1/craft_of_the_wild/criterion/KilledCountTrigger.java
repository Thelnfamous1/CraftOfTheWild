package com.Thelnfamous1.craft_of_the_wild.criterion;

import com.Thelnfamous1.craft_of_the_wild.init.CriterionInit;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class KilledCountTrigger extends SimpleCriterionTrigger<KilledCountTrigger.TriggerInstance> {
   final ResourceLocation id;

   public KilledCountTrigger(ResourceLocation pId) {
      this.id = pId;
   }

   @Override
   public ResourceLocation getId() {
      return this.id;
   }

   @Override
   public KilledCountTrigger.TriggerInstance createInstance(JsonObject pJson, ContextAwarePredicate pPredicate, DeserializationContext pDeserializationContext) {
      return new KilledCountTrigger.TriggerInstance(this.id, pPredicate, EntityPredicate.fromJson(pJson, "entity", pDeserializationContext), pJson.get("kill_count").getAsInt());
   }

   public void trigger(ServerPlayer pPlayer, Entity pEntity) {
      LootContext lootcontext = EntityPredicate.createContext(pPlayer, pEntity);
      this.trigger(pPlayer, (instance) -> instance.matches(pPlayer, lootcontext));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ContextAwarePredicate entityPredicate;
      private final int killCount;

      public TriggerInstance(ResourceLocation pCriterion, ContextAwarePredicate pPlayer, ContextAwarePredicate pEntityPredicate, int pKillCount) {
         super(pCriterion, pPlayer);
         this.entityPredicate = pEntityPredicate;
         this.killCount = pKillCount;
      }

      public static KilledCountTrigger.TriggerInstance playerKilledEntity(EntityPredicate pEntityPredicate, int amount) {
         return new KilledCountTrigger.TriggerInstance(CriterionInit.PLAYER_KILLED_ENTITY_COUNT.id, ContextAwarePredicate.ANY, EntityPredicate.wrap(pEntityPredicate), amount);
      }

      public static KilledCountTrigger.TriggerInstance playerKilledEntity(EntityPredicate.Builder pEntityPredicateBuilder, int amount) {
         return new KilledCountTrigger.TriggerInstance(CriterionInit.PLAYER_KILLED_ENTITY_COUNT.id, ContextAwarePredicate.ANY, EntityPredicate.wrap(pEntityPredicateBuilder.build()), amount);
      }


      public boolean matches(ServerPlayer pPlayer, LootContext pContext) {
         return pPlayer.getStats().getValue(Stats.ENTITY_KILLED.get(pContext.getParam(LootContextParams.THIS_ENTITY).getType())) >= this.killCount
                 && this.entityPredicate.matches(pContext);
      }

      @Override
      public JsonObject serializeToJson(SerializationContext pConditions) {
         JsonObject jsonobject = super.serializeToJson(pConditions);
         jsonobject.add("entity", this.entityPredicate.toJson(pConditions));
         jsonobject.addProperty("kill_count", this.killCount);
         return jsonobject;
      }
   }
}