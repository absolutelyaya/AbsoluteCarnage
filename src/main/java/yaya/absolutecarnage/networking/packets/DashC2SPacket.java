package yaya.absolutecarnage.networking.packets;

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import yaya.absolutecarnage.items.trinkets.WingTrinketItem;
import yaya.absolutecarnage.registries.StatRegistry;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class DashC2SPacket
{
	public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
							   PacketByteBuf buf, PacketSender responseSender)
	{
		Optional<TrinketComponent> trinketComp = TrinketsApi.getTrinketComponent(player);
		if(trinketComp.isPresent())
		{
			int direction = buf.readVarInt();
			AtomicBoolean b = new AtomicBoolean(false);
			AtomicReference<WingTrinketItem> wing = new AtomicReference<>(null);
			trinketComp.get().forEach(((slotReference, itemStack) -> {
				if(itemStack.getItem() instanceof WingTrinketItem item)
				{
					b.set(true);
					wing.set(item);
				}
			}));
			if(b.get() && wing.get() != null)
			{
				if(player.getItemCooldownManager().isCoolingDown(wing.get()))
					player.sendMessage(Text.translatable("msg.absolute_carnage.wings_cooldown"), true);
				else
				{
					Vec3d dir = new Vec3d(0, 0, -1).rotateY(-(float)Math.toRadians(player.getYaw() + 180));
					switch (direction)
					{
						case 1 -> dir = dir.rotateY((float)Math.toRadians(-90));
						case 3 -> dir = dir.negate();
						case 2 -> dir = dir.rotateY((float)Math.toRadians(90));
					}
					float power = wing.get().getPower();
					wing.get().onUse(player, direction);
					responseSender.sendPacket(new EntityVelocityUpdateS2CPacket(player.getId(), dir.multiply(power).add(0, 0.25, 0)));
					player.getItemCooldownManager().set(wing.get(), 60);
					player.incrementStat(StatRegistry.DODGE);
					player.world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_ENDER_DRAGON_FLAP, SoundCategory.PLAYERS, 1, 1.25f);
				}
			}
		}
	}
}
