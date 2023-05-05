package com.codenjoy.dojo.utils.scorestest;

import com.codenjoy.dojo.services.event.Calculator;
import com.codenjoy.dojo.services.event.EventObject;
import com.codenjoy.dojo.services.event.ScoresImpl;
import com.codenjoy.dojo.services.event.ScoresMap;
import com.codenjoy.dojo.services.settings.SettingsReader;
import lombok.SneakyThrows;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static com.codenjoy.dojo.utils.core.MockitoJunitTesting.testing;
import static java.util.stream.Collectors.joining;

public abstract class AbstractScoresTest {

    public static final String SEPARATOR_BEFORE_AFTER = " => ";
    public static final String SEPARATOR_EVENT_PARAMETERS = ",";
    public static final String SEPARATOR_EVENTS = " ";

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

    private String run(String expected) {
        String left = expected.split(SEPARATOR_BEFORE_AFTER)[0];
        String[] parts = left.split(SEPARATOR_EVENTS);

        AtomicInteger score;
        int fromIndex;
        if (isNumber(parts[0])) {
            score = new AtomicInteger(Integer.parseInt(parts[0]));
            givenScores(score.get());
            fromIndex = 1;
        } else {
            score = new AtomicInteger(scores.getScore());
            fromIndex = 0;
        }

        String scoresHistory = Arrays.asList(parts)
                .subList(fromIndex, parts.length).stream()
                .map(this::event)
                .filter(Objects::nonNull)
                .peek(scores::event)
                .map(event -> sign(scores.getScore() - score.get()))
                .peek(it -> score.set(scores.getScore()))
                .collect(joining(SEPARATOR_EVENTS));

        return String.format("%s%s%s%s%s",
                left,
                SEPARATOR_BEFORE_AFTER,
                scoresHistory,
                SEPARATOR_BEFORE_AFTER,
                scores.getScore());
    }

    private boolean isNumber(String string) {
        return string.chars().allMatch(Character::isDigit);
    }

    private String sign(int value) {
        return (value >= 0)
                ? "+" + value
                : String.valueOf(value);
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
