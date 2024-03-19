package com.Thelnfamous1.craft_of_the_wild.structure;

import com.Thelnfamous1.craft_of_the_wild.init.WorldGenInit;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.structure.SinglePieceStructure;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

import java.util.Optional;

public class StoneTalusStructure extends SinglePieceStructure {
   public static final Codec<StoneTalusStructure> CODEC = simpleCodec(StoneTalusStructure::new);

   public StoneTalusStructure(Structure.StructureSettings settings) {
      super(StoneTalusPiece::new, StoneTalusPiece.STONE_TALUS_SPAWN_WIDTH, StoneTalusPiece.STONE_TALUS_SPAWN_DEPTH, settings);
   }

   @Override
   public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
      return super.findGenerationPoint(context);
      /*
      return super.findGenerationPoint(context).filter(stub -> {
         if(context.heightAccessor() instanceof BlockGetter blockGetter){
            boolean canBurrowInto = NearestBurrowSensor.canBurrowInto(blockGetter, stub.position().below(), EntityInit.STONE_TALUS.get().getWidth(), EntityInit.STONE_TALUS.get().getHeight());
            COTWCommon.debug(Constants.DEBUG_STONE_TALUS_STRUCTURE, "Generating stone talus structure at {} ? {}", stub.position(), canBurrowInto);
            return canBurrowInto;
         }
         return false;
      });
       */
   }

   @Override
   public StructureType<?> type() {
      return WorldGenInit.STONE_TALUS_ST.get();
   }
}