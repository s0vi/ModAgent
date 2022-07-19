package dev.s0vi.modagent;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

public class ModAgent implements ModInitializer, PreLaunchEntrypoint {
    public static final String MODID = "modagent";
    public static final Logger LOGGER = LogManager.getLogger("modagent");

    public Path UNLOADED_MODS;

    @Override
    public void onInitialize() {
        UNLOADED_MODS = FabricLoader.getInstance().getGameDir().resolve("/modagent");
    }

    @Override
    public void onPreLaunch() {
        displayWarning();
    }

    private void displayWarning() {
        LOGGER.warn("========= WARNING ========");
        LOGGER.warn("THIS INSTANCE HAS ModAgent INSTALLED. ANY MODS LOADED VIA ModAgent MAY NOT FUNCTION CORRECTLY.");
        LOGGER.warn("PLEASE REMOVE THIS MOD AND LOAD ALL MODS CORRECTLY BEFORE SUBMITTING BUG REPORTS.");
        LOGGER.warn("==========================");
    }
}
