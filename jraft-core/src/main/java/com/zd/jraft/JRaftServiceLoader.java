package com.zd.jraft;

import com.zd.jraft.annotation.SPI;
import com.zd.jraft.utils.Requires;

import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

public final class JRaftServiceLoader<S> implements Iterable<S> {

    private final Class<S> service;

    private final ClassLoader loader;

    private LinkedHashMap<String, S> providers = new LinkedHashMap<>();

    private LazyIterator lookupIterator;

    private JRaftServiceLoader(final Class<S> service, final ClassLoader loader) {
        this.service = Requires.requireNonNull(service, "service interface cannot be null");
        this.loader = (loader == null) ? ClassLoader.getSystemClassLoader() : loader;
        reload();
    }

    public void reload() {
        this.providers.clear();
        this.lookupIterator = new LazyIterator(this.service, this.loader);
    }

    public S first() {
        final List<S> sortList = sort();
        if (sortList.isEmpty()) {
            throw fail(this.service, "could not find any implementation for class");
        }
        return sortList.get(0);
    }

    private static ServiceConfigurationError fail(final Class<?> service, final String msg) {
        return new ServiceConfigurationError(service.getName() + ": " + msg);
    }

    public List<S> sort() {
        final Iterator<S> it = iterator();
        final List<S> sortList = new ArrayList<>();
        while (it.hasNext()) {
            sortList.add(it.next());
        }
        if (sortList.size() <= 1) {
            return sortList;
        }
        sortList.sort((o1, o2) -> {
            final SPI o1_spi = o1.getClass().getAnnotation(SPI.class);
            final SPI o2_spi = o2.getClass().getAnnotation(SPI.class);

            final int o1_priority = o1_spi == null ? 0 : o1_spi.priority();
            final int o2_priority = o2_spi == null ? 0 : o2_spi.priority();

            return -(o1_priority - o2_priority);
        });
        return sortList;
    }

    public static <S> JRaftServiceLoader<S> load(final Class<S> service) {
        return JRaftServiceLoader.load(service, Thread.currentThread().getContextClassLoader());
    }

    public static <S> JRaftServiceLoader<S> load(final Class<S> service, final ClassLoader loader) {
        return new JRaftServiceLoader<>(service, loader);
    }

    @Override
    public Iterator<S> iterator() {
        return null;
    }

    @Override
    public void forEach(Consumer<? super S> action) {

    }

    @Override
    public Spliterator<S> spliterator() {
        return null;
    }

    private class LazyIterator implements Iterator<Class<S>>{

        Class<S>         service;
        ClassLoader      loader;
        Enumeration<URL> configs  = null;
        Iterator<String> pending  = null;
        String           nextName = null;

        private LazyIterator(Class<S> service, ClassLoader loader) {
            this.service = service;
            this.loader = loader;
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Class<S> next() {
            return null;
        }

        @Override
        public void remove() {

        }

        @Override
        public void forEachRemaining(Consumer<? super Class<S>> action) {

        }
    }
}
