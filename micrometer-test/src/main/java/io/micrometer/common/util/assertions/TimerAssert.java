package io.micrometer.common.util.assertions;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.DoubleAssert;
import org.assertj.core.api.DurationAssert;
import org.assertj.core.api.IntegerAssert;

import io.micrometer.core.instrument.Timer;

public class TimerAssert extends AbstractAssert<TimerAssert, Timer> {

    public static TimerAssert assertThat(Timer actual) {
        return new TimerAssert(actual);
    }

    public TimerAssert(Timer actual) {
        super(actual, TimerAssert.class);
    }

    public DurationAssert totalTime() {
        return toDurationAssert(actual::totalTime);
    }

    public DurationAssert max() {
        return toDurationAssert(actual::max);
    }

    public DurationAssert mean() {
        return toDurationAssert(actual::mean);
    }

    public TimerAssert hasCount(int expectedCount) {
        count().isEqualTo(expectedCount);
        return this;
    }

    public IntegerAssert count() {
        return new IntegerAssert((int)actual.count());
    }

    private static DurationAssert toDurationAssert(Function<TimeUnit, Double> accessor) {
        double nanos = accessor.apply(TimeUnit.NANOSECONDS);
        return new DurationAssert(Duration.ofNanos((long) nanos));
    }
}
