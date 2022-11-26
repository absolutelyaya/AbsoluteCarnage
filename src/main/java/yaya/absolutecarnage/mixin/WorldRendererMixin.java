package yaya.absolutecarnage.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShapes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yaya.absolutecarnage.entities.CarnagePaintingEntity;
import yaya.absolutecarnage.items.CarnagePaintingItem;
import yaya.absolutecarnage.registries.EntityRegistry;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin
{
	@Shadow @Final private MinecraftClient client;
	@Shadow @Final private BufferBuilderStorage bufferBuilders;
	
	CarnagePaintingEntity previewPainting;
	
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;drawBlockOutline(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/entity/Entity;DDDLnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"))
	public void render(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, CallbackInfo ci)
	{
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		if(player == null)
		{
			if(previewPainting != null)
			{
				previewPainting.discard();
				previewPainting = null;
			}
			return;
		}
		ItemStack stack = player.getInventory().getMainHandStack();
		if (stack != null && stack.getItem() instanceof CarnagePaintingItem)
		{
			NbtCompound nbt = stack.getSubNbt("absolute_carnage");
			if(nbt == null || !nbt.contains("variant"))
				return;
			
			BlockPos previewPos;
			Direction previewDirection;
			
			HitResult result = client.crosshairTarget;
			if(result != null && result.getType() == HitResult.Type.BLOCK)
			{
				BlockHitResult blockHit = (BlockHitResult)result;
				BlockPos blockPos = blockHit.getBlockPos();
				previewDirection = blockHit.getSide();
				previewPos = blockPos.offset(previewDirection);
				
				if(!canPlacePaintingOn(player, previewDirection, stack, blockPos))
					return;
			}
			else
				return;
			
			if(previewPainting == null)
			{
				previewPainting = new CarnagePaintingEntity(EntityRegistry.CARNAGE_PAINTING, player.world);
				previewPainting.noClip = true;
				previewPainting.checkObstruction = false;
			}
			previewPainting.setFacing(previewDirection);
			previewPainting.setVariant(Identifier.tryParse(nbt.getString("variant")));
			previewPainting.setPosition(previewPos.getX(), previewPos.getY(), previewPos.getZ());
			previewPainting.lastRenderX = previewPainting.getX();
			previewPainting.lastRenderY = previewPainting.getY();
			previewPainting.lastRenderZ = previewPainting.getZ();
			
			VertexConsumerProvider.Immediate immediate = this.bufferBuilders.getEntityVertexConsumers();
			boolean b = previewPainting.canStayAttached();
			
			Vec3d camPos = camera.getPos();
			WorldRenderer.drawCuboidShapeOutline(matrices, immediate.getBuffer(RenderLayer.getLines()),
					VoxelShapes.cuboid(previewPainting.getBoundingBox()),
					0 - camPos.x, 0 - camPos.y, 0 - camPos.z,
					b ? 0f : 0.5f, b ? 0.5f : 0f, 0f, 0.5f);
		}
		else if(previewPainting != null)
		{
			previewPainting.discard();
			previewPainting = null;
		}
	}
	
	private boolean canPlacePaintingOn(PlayerEntity player, Direction side, ItemStack stack, BlockPos pos)
	{
		return !side.getAxis().isVertical() && player.canPlaceOn(pos, side, stack);
	}
}
