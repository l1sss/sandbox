package ru.slisenko.screensaver;

import javafx.util.Pair;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class PeriodicalScopeConfigurer implements Scope {
    private static final int LIFE_TIME_SECONDS = 3;
    private Map<String, Pair<LocalTime, Object>> map = new HashMap<>();

    @Override
    public Object get(String s, ObjectFactory<?> objectFactory) {
        if (map.containsKey(s)) {
            Pair<LocalTime, Object> pair = map.get(s);
            if (getLifetime(pair) > LIFE_TIME_SECONDS) {
                map.put(s, new Pair<>(LocalTime.now(), objectFactory.getObject()));
            }
        } else {
            map.put(s, new Pair<>(LocalTime.now(), objectFactory.getObject()));
        }
        return map.get(s).getValue();
    }

    private long getLifetime(Pair<LocalTime, Object> pair) {
        LocalTime bornTime = pair.getKey();
        Duration between = Duration.between(bornTime, LocalTime.now());
        return between.getSeconds();
    }

    @Override
    public Object remove(String s) {
        return null;
    }

    @Override
    public void registerDestructionCallback(String s, Runnable runnable) {

    }

    @Override
    public Object resolveContextualObject(String s) {
        return null;
    }

    @Override
    public String getConversationId() {
        return null;
    }
}
