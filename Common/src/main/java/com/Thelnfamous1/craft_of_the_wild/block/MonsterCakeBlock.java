package com.Thelnfamous1.craft_of_the_wild.block;

import com.Thelnfamous1.craft_of_the_wild.recipe.PotionInfusionRecipe;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MonsterCakeBlock extends BaseEntityBlock{
    public static final int MAX_BITES = 3;
    public static final IntegerProperty BITES = IntegerProperty.create("bites", 0, MAX_BITES);
    protected static final VoxelShape[] SHAPE_BY_BITE = Util.make(new VoxelShape[MAX_BITES + 1], voxelShapes -> {
        voxelShapes[0] = makeShape0();
        voxelShapes[1] = makeShape1();
        voxelShapes[2] = makeShape2();
        voxelShapes[3] = makeShape3();
    });
    public static final int CAKE_NUTRITION = 4;
    public static final float CAKE_SATURATION = 0.1F;

    public MonsterCakeBlock(Properties $$0) {
        super($$0);
        this.registerDefaultState(this.stateDefinition.any().setValue(BITES, 0));
    }

    public static VoxelShape makeShape0(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0.0625, 0.9375, 0.9375, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.9375, 0.625, 0.3125, 1.1875, 0.875, 0.5625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(1.0625, 0.875, 0.3125, 1.1875, 1.25, 0.5625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(1, 1.1875, 0.3125, 1.0625, 1.25, 0.5625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(-0.0625, 1.1875, 0.3125, 0, 1.25, 0.5625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(-0.1875, 0.875, 0.3125, -0.0625, 1.25, 0.5625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(-0.1875, 0.625, 0.3125, 0.0625, 0.875, 0.5625), BooleanOp.OR);

        return shape;
    }

    public static VoxelShape makeShape1(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.5, 0, 0.0625, 0.9375, 0.9375, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0.5, 0.5, 0.9375, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(1, 1.1875, 0.3125, 1.0625, 1.25, 0.5625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.9375, 0.625, 0.3125, 1.1875, 0.875, 0.5625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(1.0625, 0.875, 0.3125, 1.1875, 1.25, 0.5625), BooleanOp.OR);

        return shape;
    }

    public static VoxelShape makeShape2(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0.375, 0.9375, 0.9375, 0.9375), BooleanOp.OR);

        return shape;
    }

    public static VoxelShape makeShape3(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.4375, 0, 0.4375, 0.9375, 0.9375, 0.9375), BooleanOp.OR);

        return shape;
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return MonsterCakeBlock.SHAPE_BY_BITE[$$0.getValue(BITES)];
    }

    @Override
    public InteractionResult use(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, InteractionHand $$4, BlockHitResult $$5) {
        ItemStack $$6 = $$3.getItemInHand($$4);

        if ($$1.isClientSide) {
            if (MonsterCakeBlock.eat($$1, $$2, $$0, $$3).consumesAction()) {
                return InteractionResult.SUCCESS;
            }

            if ($$6.isEmpty()) {
                return InteractionResult.CONSUME;
            }
        }

        return MonsterCakeBlock.eat($$1, $$2, $$0, $$3);
    }

    protected static InteractionResult eat(Level $$0, BlockPos $$1, BlockState $$2, Player $$3) {
        if (!$$3.canEat(false)) {
            return InteractionResult.PASS;
        } else {
            $$3.awardStat(Stats.EAT_CAKE_SLICE);
            $$3.getFoodData().eat(CAKE_NUTRITION, CAKE_SATURATION);
            if($$0.getBlockEntity($$1) instanceof MonsterCakeBlockEntity monsterCakeBlockEntity){
                monsterCakeBlockEntity.applyPotionEffects($$0, $$3);
            }
            int $$4 = $$2.getValue(BITES);
            $$0.gameEvent($$3, GameEvent.EAT, $$1);
            if ($$4 < MAX_BITES) {
                $$0.setBlock($$1, $$2.setValue(BITES, $$4 + 1), 3);
            } else {
                $$0.removeBlock($$1, false);
                $$0.gameEvent($$3, GameEvent.BLOCK_DESTROY, $$1);
            }

            return InteractionResult.SUCCESS;
        }
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        return $$1 == Direction.DOWN && !$$0.canSurvive($$3, $$4) ? Blocks.AIR.defaultBlockState() : super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        return $$1.getBlockState($$2.below()).isSolid();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(BITES);
    }

    @Override
    public int getAnalogOutputSignal(BlockState $$0, Level $$1, BlockPos $$2) {
        return getOutputSignal($$0.getValue(BITES));
    }

    public static int getOutputSignal(int $$0) {
        return (7 - $$0) * 2;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState $$0) {
        return true;
    }

    @Override
    public boolean isPathfindable(BlockState $$0, BlockGetter $$1, BlockPos $$2, PathComputationType $$3) {
        return false;
    }

    @Override
    public RenderShape getRenderShape(BlockState $$0) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new MonsterCakeBlockEntity(blockPos, blockState);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state,
                            @Nullable LivingEntity placer, ItemStack stack) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);

            if (be instanceof MonsterCakeBlockEntity potionBE) {
                CompoundTag blockEntityData = BlockItem.getBlockEntityData(stack);
                if(blockEntityData == null){
                    blockEntityData = new CompoundTag();
                }
                if(!PotionInfusionRecipe.hasInfusedPotion(blockEntityData) && PotionInfusionRecipe.hasInfusedPotion(stack)){
                    PotionInfusionRecipe.setInfusedPotion(blockEntityData, PotionInfusionRecipe.getInfusedPotion(stack));
                    BlockItem.setBlockEntityData(stack, be.getType(), blockEntityData);
                }
                potionBE.load(blockEntityData);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack $$0, @Nullable BlockGetter $$1, List<Component> $$2, TooltipFlag $$3) {
        CompoundTag dataContainingInfusedPotion = PotionInfusionRecipe.hasInfusedPotion($$0) ? $$0.getTag() : BlockItem.getBlockEntityData($$0);
        PotionUtils.addPotionTooltip(PotionInfusionRecipe.getAllInfusedEffects(dataContainingInfusedPotion), $$2, 1.0F);

    }
}
