package de.unijena.bioinf.babelms.descriptor;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.HashMap;
import java.util.HashSet;

/**
 *
 */
public class DescriptorRegistry {

    private final HashMap<Class, DescriptorMap> registry;

    private static DescriptorRegistry SINGLETON;
    static {
        SINGLETON = new DescriptorRegistry();
        DefaultDescriptors.addAll(SINGLETON);
    }

    public static DescriptorRegistry getInstance() {
        return SINGLETON;
    }

    DescriptorRegistry() {
        this.registry = new HashMap<Class, DescriptorMap>();
    }

    public <AnnotationType, Annotation> void put(Class<AnnotationType> annotationType, Class<Annotation> annotationClass, Descriptor<Annotation> descriptor) {
        final String[] keywords = descriptor.getKeywords();
        synchronized (registry) {
            final DescriptorMap map;
            if (registry.containsKey(annotationType)) {
                map = registry.get(annotationType);
            } else {
                map = new DescriptorMap();
                registry.put(annotationType, map);
            }
            map.put(annotationClass, descriptor, keywords);
        }
    }

    public <AnnotationType> Descriptor[] getByKeywords(Class<AnnotationType> annotationType, String[] keywords) {
        synchronized (registry) {
            final DescriptorMap map =registry.get(annotationType);
            if (map == null) return new Descriptor[0];
            else return map.getByKeyword(keywords);
        }
    }

    public <AnnotationType, Annotation> Descriptor<Annotation> get(Class<AnnotationType> annotationType, Class<Annotation> annotoAnnotationClass) {
        synchronized (registry) {
            final DescriptorMap map =registry.get(annotationType);
            return map.get(annotoAnnotationClass);
        }
    }

    private static class DescriptorMap {

        private HashMap<Class, Descriptor<Object>> hashmap;
        private Multimap<String, Descriptor<Object>> keywordMap;

        public DescriptorMap() {
            this.hashmap = new HashMap<Class, Descriptor<Object>>();
            this.keywordMap = ArrayListMultimap.create(16, 4);
        }

        private <Annotation> void put(Class<Annotation> key, Descriptor<Annotation> descriptor, String[] keywords) {
            hashmap.put(key, (Descriptor<Object>)descriptor);
            for (String s : keywords) keywordMap.put(s, (Descriptor<Object>)descriptor);
        }

        private <Annotation> Descriptor<Annotation> get(Class<Annotation> annotationClass) {
            return (Descriptor<Annotation>)hashmap.get(annotationClass);
        }

        private Descriptor[] getByKeyword(String[] keywords) {
            final HashSet<Descriptor> descriptors = new HashSet<Descriptor>();
            for (String k : keywords) {
                descriptors.addAll(keywordMap.get(k));
            }
            return descriptors.toArray(new Descriptor[descriptors.size()]);
        }

    }

}
