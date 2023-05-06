package com.codenjoy.dojo.utils.scorestest;

import com.codenjoy.dojo.services.event.Calculator;
import com.codenjoy.dojo.services.event.EventObject;
import com.codenjoy.dojo.services.event.ScoresImpl;
import com.codenjoy.dojo.services.event.ScoresMap;
import com.codenjoy.dojo.services.settings.SettingsReader;
import lombok.SneakyThrows;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.codenjoy.dojo.utils.core.MockitoJunitTesting.testing;
import static com.codenjoy.dojo.utils.scorestest.AbstractScoresTest.Separators.*;
import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

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
            int score = parseInt(line.split(GIVEN)[0]);
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

        String[] params = line.split(PARAMETERS);

        if (events() != null) {
            return getEvent(params);
        }

        return getEventType(line);
    }

    private Enum getEventType(String name) {
        return Enum.valueOf(eventTypes(), name);
    }

    @SneakyThrows
    private EventObject getEvent(String... params){
        List values = Stream.of(params)
                .skip(1)
                .map(value -> isNumber(value) ? parseInt(value) : parseBoolean(value))
                .collect(toList());
        values.add(0, getEventType(params[0]));

        List<Class> classes = Stream.of(params)
                .skip(1)
                .map(value -> isNumber(value) ? int.class : boolean.class) // TODO what about other primitives
                .collect(toList());
        classes.add(0, eventTypes());

        return events().getDeclaredConstructor(classes.toArray(new Class[]{}))
                .newInstance(values.toArray());
    }

    private boolean isNumber(String value) {
        try {
            parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}