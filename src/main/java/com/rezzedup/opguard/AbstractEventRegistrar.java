package com.rezzedup.opguard;

/*
 *  AbstractEventRegistrar created by jkcclemens on GitHub 
 *  Repository: https://github.com/jkcclemens/AbstractEventRegistrar
 *  License: GPL-3.0 https://github.com/jkcclemens/AbstractEventRegistrar/blob/master/LICENSE
 */

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * A class to register listeners for abstract events in Bukkit.
 */
public class AbstractEventRegistrar {
    
    private final Plugin plugin;
    
    /**
     * Initializes the registrar with the plugin that is utilizing it.
     *
     * @param plugin Plugin using the registrar
     */
    public AbstractEventRegistrar(Plugin plugin) {
        this.plugin = plugin;
    }
    
    private ArrayList<Class<?>> getClassesForPackage(Package pkg) {
        final ArrayList<Class<?>> classes = new ArrayList<>();
        final String pkgname = pkg.getName();
        final String relPath = pkgname.replace('.', '/');
        // Get a File object for the package
        final URL resource = ClassLoader.getSystemClassLoader().getResource(relPath);
        if (resource == null) throw new RuntimeException("Unexpected problem: No resource for " + relPath);
        resource.getPath();
        if (resource.toString().startsWith("jar:")) this.processJarfile(resource, pkgname, classes);
        else this.processDirectory(new File(resource.getPath()), pkgname, classes);
        return classes;
    }
    
    private <T> List<Class<? extends T>> getSubTypesOf(Class<T> clazz, List<Class<?>> classes) {
        final List<Class<? extends T>> goodClasses = new ArrayList<>();
        for (Class<?> clazzz : classes) {
            if (!clazz.isAssignableFrom(clazzz)) continue;
            //noinspection unchecked
            goodClasses.add((Class<? extends T>) clazzz);
        }
        return goodClasses;
    }
    
    private Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unexpected ClassNotFoundException loading class '" + className + "'");
        }
    }
    
    private void processDirectory(File directory, String pkgname, ArrayList<Class<?>> classes) {
        final String[] files = directory.list();
        for (final String fileName : files) {
            String className = null;
            if (fileName.endsWith(".class")) className = pkgname + '.' + fileName.substring(0, fileName.length() - 6);
            if (className != null) classes.add(this.loadClass(className));
            final File subdir = new File(directory, fileName);
            if (subdir.isDirectory()) this.processDirectory(subdir, pkgname + '.' + fileName, classes);
        }
    }
    
    private void processJarfile(URL resource, String pkgname, ArrayList<Class<?>> classes) {
        final String relPath = pkgname.replace('.', '/');
        final String resPath = resource.getPath();
        final String jarPath = resPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
        final JarFile jarFile;
        try {
            jarFile = new JarFile(jarPath);
        } catch (IOException e) {
            throw new RuntimeException("Unexpected IOException reading JAR File '" + jarPath + "'", e);
        }
        final Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            final JarEntry entry = entries.nextElement();
            final String entryName = entry.getName();
            String className = null;
            if (entryName.endsWith(".class") && entryName.startsWith(relPath) && entryName.length() > (relPath.length() + "/".length())) {
                className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");
            }
            if (className != null) classes.add(this.loadClass(className));
        }
    }
    
    /**
     * Registers an abstract listener for the plugin this class was initialized with.
     *
     * @param l Listener to register
     */
    public void registerAbstractListener(final Listener l) {
        this.registerAbstractListener(l, this.plugin);
    }
    
    /**
     * Registers an abstract listener for the given plugin.
     *
     * @param l Listener to register
     * @param p Plugin to register under
     */
    public void registerAbstractListener(final Listener l, final Plugin p) {
        final PluginManager pm = p.getServer().getPluginManager();
        final List<Class<? extends Event>> events = this.getSubTypesOf(Event.class, this.getClassesForPackage(Package.getPackage("org.bukkit.event")));
        for (final Class<? extends Event> eventInClasspath : events) {
            for (final Method m : l.getClass().getDeclaredMethods()) {
                final AbstractEventHandler aeh = m.getAnnotation(AbstractEventHandler.class);
                if (aeh == null) continue;
                final Class<?>[] params = m.getParameterTypes();
                if (params.length != 1) continue;
                final Class<?> methodEvent = params[0];
                // make sure the event in this method is an instance of the current event
                if (!methodEvent.isAssignableFrom(eventInClasspath)) continue;
                try {
                    // ensure the current event can have handlers
                    eventInClasspath.getDeclaredMethod("getHandlerList");
                } catch (ReflectiveOperationException ex) {
                    continue;
                }
                // make sure the current event is an instance of the method event
                if (eventInClasspath.getClass().isAssignableFrom(methodEvent)) continue;
                pm.registerEvent(eventInClasspath, l, aeh.priority(), new EventExecutor() {
                    @Override
                    public void execute(final Listener listener, final Event event) throws EventException {
                        if (aeh.ignoreCancelled() && event instanceof Cancellable && ((Cancellable) event).isCancelled()) {
                            return;
                        }
                        try {
                            m.invoke(l, event);
                        } catch (final ReflectiveOperationException ex) {
                            ex.printStackTrace();
                        }
                    }
                }, p);
            }
        }
    }
    
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface AbstractEventHandler {
        
        boolean ignoreCancelled() default false;
        
        EventPriority priority() default EventPriority.NORMAL;
    }
    
}
