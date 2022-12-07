package com.github.tanokun.pluginsui.util

import java.net.JarURLConnection
import java.util.jar.JarEntry

fun getClasses(rootClass: Class<*>): ArrayList<Class<*>> {
    val r = ArrayList<Class<*>>()

    val rootPackage = rootClass.getPackage()
    val resourceName = rootPackage.name.replace('.', '/')
    val classLoader = rootClass.classLoader

    val root = classLoader.getResource(resourceName)

    val classes = (root?.openConnection() as JarURLConnection).jarFile.entries()
    val iterator = classes.iterator()
    while (iterator.hasNext()) {
        val file = iterator.next()
        if (!file.name.endsWith(".class")) continue
        val entry = file as JarEntry
        val name = entry.name.replace('/', '.').replace(".class$".toRegex(), "")
        try {
            r.add(Class.forName(name))
        } catch (e: Throwable) {
            if (e is NoClassDefFoundError) continue
            if (e is ClassNotFoundException) continue
            continue
        }
    }

    return r;
}