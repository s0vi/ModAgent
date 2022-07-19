package dev.s0vi.modagent.instrument.transformer;

import dev.s0vi.modagent.ModAgent;
import javassist.*;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class KnotTransformer implements ClassFileTransformer {
    /** The internal form class name of the class to transform */
    private String targetClassName;
    /** The class loader of the class we want to transform */
    private ClassLoader targetClassLoader;
    private final Logger LOGGER = ModAgent.LOGGER;

    public KnotTransformer(String targetClassName, ClassLoader targetClassLoader) {
        this.targetClassLoader = targetClassLoader;
        this.targetClassName = targetClassName;
    }

    @Override
    public byte[] transform(
            ClassLoader loader,
            String className,
            Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain,
            byte[] classfileBuffer)
            throws IllegalClassFormatException
    {
//        return ClassFileTransformer.super.transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
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

                //Adds call to isDev to set Knot field.
                taintIsDev(cc);

                //Adds function that returns the classLoader
                CtField loaderField = cc.getDeclaredField("classLoader");
                cc.addMethod(CtNewMethod.getter("modAgentGetClassLoader", loaderField));

                byteCode = cc.toBytecode();
                cc.detach();
            } catch (NotFoundException e) {
                e.printStackTrace();
            } catch (CannotCompileException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return byteCode;
    }

    private void taintIsDev(CtClass cc) throws NotFoundException, CannotCompileException {
        CtMethod isDev = cc.getDeclaredMethod("isDevelopment");
        CtField knotField = CtField.make("public static Knot KNOT = null;", cc);//create knot field
        isDev.insertAt(332, "KNOT = this;");
        cc.addMethod(CtNewMethod.getter("modAgentGetKnot", knotField));
    }
}
