package com.mengma.minicat.loader;

/**
 * @author fgm
 * @description  class资源
 * @date 2020-03-28
 ***/
public class ResourceEntry {

    /**
     * The "last modified" time of the origin file at the time this resource
     * was loaded, in milliseconds since the epoch.
     */
    public long lastModified = -1;


    /**
     * Loaded class.
     */
    public volatile Class<?> loadedClass = null;

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public Class<?> getLoadedClass() {
        return loadedClass;
    }

    public void setLoadedClass(Class<?> loadedClass) {
        this.loadedClass = loadedClass;
    }
}
