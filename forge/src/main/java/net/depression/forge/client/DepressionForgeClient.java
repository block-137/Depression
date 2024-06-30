package net.depression.forge.client;

import net.depression.Depression;
import net.depression.client.DepressionClient;
import net.depression.forge.config.ClientConfig;
import net.minecraftforge.api.distmarker.Dist;
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
        ClientConfig.load();
    }

}
