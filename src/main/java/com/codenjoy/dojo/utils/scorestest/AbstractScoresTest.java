package com.codenjoy.dojo.utils.scorestest;

import com.codenjoy.dojo.services.event.Calculator;
import com.codenjoy.dojo.services.event.EventObject;
import com.codenjoy.dojo.services.event.ScoresImpl;
import com.codenjoy.dojo.services.event.ScoresMap;
import com.codenjoy.dojo.services.settings.SettingsReader;
import lombok.SneakyThrows;

import java.util.Arrays;
import java.util.function.Function;

import static com.codenjoy.dojo.utils.core.MockitoJunitTesting.testing;
import static com.codenjoy.dojo.utils.scorestest.AbstractScoresTest.Separators.*;
import static java.util.stream.Collectors.joining;

public abstract class AbstractScoresTest {

    public static class Separators {
        public static final String GIVEN = ":";
        public static final String PARAMETERS = ",";
        public static final String SCORES = ">";
    }
    public static final String COMMAND_CLEAN = "(CLEAN)";

    protected ScoresImpl scores;
    protected SettingsReader settings = settings();

    private void givenScores(int score) {
        scores = new ScoresImpl<>(score, new Calculator<>(scores(settings)));
    }

    @SneakyThrows
    private ScoresMap<?> scores(SettingsReader settings) {
        return scores().getDeclaredConstructor(SettingsReader.class)
                .newInstance(settings);
    }

    protected abstract Class<? extends ScoresMap<?>> scores();

    protected abstract SettingsReader settings();

    protected Class<? extends EventObject> events() {
        return null;
    }

    protected abstract Class<? extends Enum> eventTypes();

    public void assertEvents(String expected) {
        String actual = forAll(expected, this::run);
        testing().assertEquals(expected, actual);
    }

    private String forAll(String expected, Function<String, String> lineProcessor) {
        return Arrays.stream(expected.split("\n"))
                .map(lineProcessor)
                .collect(joining("\n"));
    }

    private String sign(int value) {
        return (value >= 0)
                ? "+" + value
                : String.valueOf(value);
    }

    private String run(String line) {
        if (line.endsWith(GIVEN)) {
            int score = Integer.parseInt(line.split(GIVEN)[0]);
            givenScores(score);
            return line;
        }

        int before = scores.getScore();
        String eventName = line.split(SCORES)[0].trim();
        Object event = event(eventName);
        if (event != null) {
            scores.event(event);
        }

        return String.format("%s > %s = %s",
                eventName,
                sign(scores.getScore() - before),
                scores.getScore());
    }

    private Object event(String line) {
        if (line.equals(COMMAND_CLEAN)) {
            scores.clear();
            return null;
        }

        if (line.contains(PARAMETERS)) {
            String name = line.split(PARAMETERS)[0];
            String value = line.split(PARAMETERS)[1];

            return getEvent(name, value);
        }

        if (events() != null) {
            return getEvent(line);
        }

        return getEventType(line);
    }

    @SneakyThrows
    private EventObject getEvent(String name) {
        Enum type = getEventType(name);

        return events().getDeclaredConstructor(eventTypes())
                .newInstance(type);
    }

    private Enum getEventType(String name) {
        return Enum.valueOf(eventTypes(), name);
    }

    @SneakyThrows
    private EventObject getEvent(String name, String valueString){
        Enum type = getEventType(name);
        int value = Integer.parseInt(valueString);

        return events().getDeclaredConstructor(eventTypes(), int.class)
                .newInstance(type, value);
    }
}