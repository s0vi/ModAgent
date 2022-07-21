package dev.s0vi.modagent.instrument.transformer;

import dev.s0vi.modagent.ModAgent;
import javassist.*;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class KnotClassLoaderInterfaceTransformer implements ClassFileTransformer {
    /** The internal form class name of the class to transform */
    private String targetClassName;
    /** The class loader of the class we want to transform */
    private ClassLoader targetClassLoader;
    private final Logger LOGGER = ModAgent.LOGGER;

    public KnotClassLoaderInterfaceTransformer(String targetClassName, ClassLoader targetClassLoader) {
        this.targetClassLoader = targetClassLoader;
        this.targetClassName = targetClassName;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        byte[] byteCode = classfileBuffer;
        String finalTargetClassName = this.targetClassName.replaceAll("\\.", "/");
        //stops us from transforming the wrong class
        if(!className.equals(finalTargetClassName)) {
            return byteCode;
        }

        if(className.equals(finalTargetClassName) && loader.equals(targetClassLoader)) {
            LOGGER.info("Transforming class Knot");
            try {
                ClassPool cp = ClassPool.getDefault();
                CtClass cc = cp.get(targetClassName);

                cc.setModifiers(Modifier.setPublic(cc.getModifiers()));

                cc.toBytecode();
            } catch (NotFoundException | IOException | CannotCompileException e) {
                throw new RuntimeException(e);
            }
        }
        return byteCode;
    }
}
