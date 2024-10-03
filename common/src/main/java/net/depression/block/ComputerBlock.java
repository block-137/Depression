package net.depression.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class ComputerBlock extends HorizontalDirectionalBlock {
    public ComputerBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion());
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));

    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return this.defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return switch (blockState.getValue(FACING)) {
            case NORTH -> NORTH_SHAPE;
            case EAST -> EAST_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            default -> super.getCollisionShape(blockState, blockGetter, blockPos, collisionContext);
        };
    }

    public static final VoxelShape NORTH_SHAPE = Shapes.or(
            Block.box(0, 0, 3, 3, 2, 7),
            Block.box(4, 0, 2, 16, 1, 8),
            Block.box(1, 0, 9, 15, 15, 15)
    );
    public static final VoxelShape EAST_SHAPE = Shapes.or(
            Block.box(9, 0, 0, 13, 2, 3),
            Block.box(8, 0, 4, 14, 1, 16),
            Block.box(1, 0, 1, 7, 15, 15)
    );
    public static final VoxelShape SOUTH_SHAPE = Shapes.or(
            Block.box(13, 0, 9, 16, 2, 13),
            Block.box(0, 0, 8, 12, 1, 14),
            Block.box(1, 0, 1, 15, 15, 7)
    );
    public static final VoxelShape WEST_SHAPE = Shapes.or(
            Block.box(3, 0, 13, 7, 2, 16),
            Block.box(2, 0, 0, 8, 1, 12),
            Block.box(9, 0, 1, 15, 15, 15)
    );

}
