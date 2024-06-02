package net.depression.forge.client;

import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.networking.NetworkManager;
import net.depression.Depression;
import net.depression.client.ClientMentalStatus;
import net.depression.client.ClientActionbarHint;
import net.depression.client.DepressionClient;
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
    //若运行端位为客户端，则初始化客户端的对象
    @SubscribeEvent
    public static void onInitializeClient(FMLClientSetupEvent event) {
        DepressionClient.onInitializeClient();
    }

}
