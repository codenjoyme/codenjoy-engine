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
import static java.util.stream.Collectors.joining;

public abstract class AbstractScoresTest {

    public static final String SEPARATOR_GIVEN_SCORES = ":";
    public static final String SEPARATOR_EVENT_PARAMETERS = ",";
    public static final String SEPARATOR_EVENT_DATA = " > ";
    public static final String SEPARATOR_TOTAL_SCORES = " = ";

    protected ScoresImpl scores;
    protected SettingsReader settings = settings();

    private void givenScores(int score) {
        scores = new ScoresImpl<>(score, new Calculator<>(scores().apply(settings)));
    }

    protected abstract Function<SettingsReader, ? extends ScoresMap<?>> scores();

    protected abstract SettingsReader settings();

    protected abstract Class<? extends EventObject> events();

    protected Class<? extends Enum> eventTypes() {
        return null;
    }

    public void assertEvents(String expected) {
        String actual = forAll(expected, this::run);
        testing().assertEquals(expected, actual);
    }

    private String forAll(String expected, Function<String, String> lineProcessor) {
        return Arrays.stream(expected.split("\n"))
                .map(lineProcessor)
                .collect(joining("\n"));
    }

    private boolean isNumber(String string) {
        return string.chars().allMatch(Character::isDigit);
    }

    private String sign(int value) {
        return (value >= 0)
                ? "+" + value
                : String.valueOf(value);
    }

    private String run(String expected) {
        if (expected.endsWith(SEPARATOR_GIVEN_SCORES)) {
            int score = Integer.parseInt(expected.split(SEPARATOR_GIVEN_SCORES)[0]);
            givenScores(score);
            return expected;
        }

        int score = scores.getScore();
        String eventName = expected.split(SEPARATOR_EVENT_DATA)[0];
        EventObject event = event(eventName);
        if (event != null) {
            scores.event(event);
        }
        String result = String.format("%s%s%s%s%s",
                eventName,
                SEPARATOR_EVENT_DATA,
                sign(scores.getScore() - score),
                SEPARATOR_TOTAL_SCORES,
                scores.getScore());

        return result;
    }

    @SneakyThrows
    private EventObject event(String line) {
        if (line.equals("<CLEAN>")) {
            scores.clear();
            return null;
        }
        if (line.contains(SEPARATOR_EVENT_PARAMETERS)) {
            String name = line.split(SEPARATOR_EVENT_PARAMETERS)[0];

            Enum type = Enum.valueOf(eventTypes(), name);
            int value = Integer.parseInt(line.split(SEPARATOR_EVENT_PARAMETERS)[1]);
            return events().getDeclaredConstructor(eventTypes(), int.class)
                    .newInstance(type, value);
        }

        Enum type = Enum.valueOf(eventTypes(), line);
        return events().getDeclaredConstructor(eventTypes())
                .newInstance(type);
    }

}
