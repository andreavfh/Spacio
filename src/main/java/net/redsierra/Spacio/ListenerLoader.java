package net.redsierra.Spacio;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.reflections.Reflections;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ListenerLoader {

    public static List<ListenerAdapter> loadListeners(String packageName) {
        Reflections reflections = new Reflections(packageName);

        Set<Class<? extends ListenerAdapter>> listenerClasses = reflections.getSubTypesOf(ListenerAdapter.class);

        return listenerClasses.stream()
                .map(cls -> {
                    try {
                        ListenerAdapter instance = cls.getDeclaredConstructor().newInstance();
                        System.out.println("Loaded " + cls.getName());
                        return instance;
                    } catch (Exception e) {
                        System.err.println("Can't load " + cls.getName());
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
