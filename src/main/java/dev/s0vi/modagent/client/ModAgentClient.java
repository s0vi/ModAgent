package dev.s0vi.modagent.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

@Environment(EnvType.CLIENT)
public class ModAgentClient implements ClientModInitializer, PreLaunchEntrypoint {


    @Override
    public void onInitializeClient() {
        displayWarning();

        UNLOADED_MODS = FabricLoader.getInstance().getGameDir().resolve("/modagent");
    }


}
