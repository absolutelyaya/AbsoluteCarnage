package yaya.absolutecarnage.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import yaya.absolutecarnage.AbsoluteCarnage;
import yaya.absolutecarnage.networking.packets.DashC2SPacket;

public class ModPackets
{
	public static final Identifier DASH_ID = new Identifier(AbsoluteCarnage.MOD_ID, "dash");
	
	public static void registerC2SPackets()
	{
		ServerPlayNetworking.registerGlobalReceiver(DASH_ID, DashC2SPacket::receive);
	}
}
