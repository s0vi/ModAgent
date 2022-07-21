package dev.s0vi.modagent.instrument;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import dev.s0vi.modagent.ModAgent;
import dev.s0vi.modagent.instrument.transformer.KnotClassLoaderInterfaceTransformer;
import dev.s0vi.modagent.instrument.transformer.KnotTransformer;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class InstrumentationAgent {
    private static final List<Path> modsToLoad = new ArrayList<>();
    private static final Path agentFile = Path.of("TBD");

    public static void addModToLoad(Path p) {
        modsToLoad.add(p);
    }

    private static final ClassLoader classLoader = FabricLoader.getInstance().getClass().getClassLoader();

    public static void transform() {
        try {
            VirtualMachine jvm = VirtualMachine.attach(getPID());
            jvm.loadAgent(agentFile.toFile().getAbsolutePath());
            jvm.detach();
        } catch (AttachNotSupportedException e) {
            ModAgent.LOGGER.error("Attaching not supported!");
            e.printStackTrace();
        } catch (IOException | AgentInitializationException e) {
            e.printStackTrace();
        } catch (AgentLoadException e) {
            ModAgent.LOGGER.error("Agent load not supported!");
            e.printStackTrace();
        }
    }

    public static String getPID() {
        return String.valueOf(ProcessHandle.current().pid());
    }

    public static void premain(String agentArgs, Instrumentation inst) {
        INFO("Premain method called! This shouldn't ever happen, maybe there's another agent?");
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        INFO("Agentmain method called");
        INFO("Modifying KnotClassLoaderInterface");
        inst.addTransformer(new KnotClassLoaderInterfaceTransformer("net.fabricmc.loader.impl.launch.knot.KnotClassLoaderInterface", classLoader), true);
        INFO("Modifying Knot... things could go very wrong right now!");
        inst.addTransformer(new KnotTransformer("net.fabricmc.loader.impl.launch.knot.Knot", classLoader), true);
//        inst.retransformClasses();
    }

    private static void INFO(String msg) {
        ModAgent.LOGGER.info(String.format("[Agent] %s", msg));
    }
}
