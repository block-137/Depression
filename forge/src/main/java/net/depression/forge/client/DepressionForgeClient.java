package net.depression.forge.client;

import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.networking.NetworkManager;
import net.depression.Depression;
import net.depression.client.ClientMentalStatus;
import net.depression.client.ClientActionbarHint;
import net.depression.network.CloseEyePacket;
import net.depression.network.MentalStatusPacket;
import net.depression.network.ActionbarHintPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Depression.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class DepressionForgeClient
{
    public static final ClientMentalStatus clientMentalStatus = new ClientMentalStatus();
    public static final ClientActionbarHint clientActionbarHint = new ClientActionbarHint();
    //若运行端位为客户端，则初始化客户端的对象
    @SubscribeEvent
    public static void onInitializeClient(FMLClientSetupEvent event) {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                MentalStatusPacket.EMOTION_PACKET, clientMentalStatus::receiveEmotionPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                MentalStatusPacket.MENTAL_HEALTH_PACKET, clientMentalStatus::receiveMentalHealthPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                ActionbarHintPacket.PTSD_FROM_PACKET, clientActionbarHint::receivePTSDFormPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                ActionbarHintPacket.PTSD_DISPERSE_PACKET, clientActionbarHint::receivePTSDDispersePacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                ActionbarHintPacket.INSOMNIA_PACKET, clientActionbarHint::receiveInsomniaPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                ActionbarHintPacket.MENTAL_FATIGUE_PACKET, clientActionbarHint::receiveMentalFatiguePacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                CloseEyePacket.CLOSE_EYE_PACKET, clientMentalStatus.mentalIllness::receiveCloseEyePacket);
        ClientGuiEvent.RENDER_HUD.register(clientMentalStatus::renderHud);
    }

    public static ResourceLocation fade_in_blur = new ResourceLocation("shaders/post/fade_in_blur.json");

}
