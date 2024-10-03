package net.depression.neoforge.client;

import net.depression.Depression;
import net.depression.client.DepressionClient;
import net.depression.neoforge.config.ClientConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;


@Mod.EventBusSubscriber(modid = Depression.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class DepressionNeoForgeClient
{
    //若运行端位为客户端，则初始化客户端的对象
    @SubscribeEvent
    public static void onInitializeClient(FMLClientSetupEvent event) {
        DepressionClient.onInitializeClient();
        ClientConfig.load();
    }

}
