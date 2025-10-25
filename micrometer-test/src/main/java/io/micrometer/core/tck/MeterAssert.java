package io.micrometer.core.tck;

import io.micrometer.core.instrument.*;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.DoubleAssert;
import org.assertj.core.api.DurationAssert;
import org.assertj.core.api.ListAssert;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MeterAssert<METER extends Meter> extends AbstractAssert<MeterAssert<METER>, METER> {

    protected MeterAssert(METER actual) {
        super(actual, MeterAssert.class);
    }

    public static <M extends Meter> MeterAssert assertThat(M actual) {
        return new MeterAssert(actual);
    }

    public static <M extends Meter> MeterAssert then(M actual) {
        return new MeterAssert(actual);
    }

    public ListAssert<Double> measures() {
        List<Double> measures = StreamSupport.stream(actual.measure()
                .spliterator(), false)
            .map(Measurement::getValue)
            .collect(Collectors.toList());
        return ListAssert.assertThatList(measures);
    }

    public CounterAssert counter() {
        hasType(Meter.Type.COUNTER);
        return new CounterAssert((Counter) actual);
    }

    public TimerAssert timer() {
        hasType(Meter.Type.TIMER);
        return new TimerAssert((Timer) actual);
    }

    public void hasType(Meter.Type type) {
        // TODO
    }


    public static class CounterAssert extends MeterAssert<Counter> {

        protected CounterAssert(Counter actual) {
            super(actual);
        }

        public DoubleAssert hasCount(double expected) {
            return count().isEqualTo(expected);
        }

        public DoubleAssert count() {
            return new DoubleAssert(actual.count());
        }
    }


    public static class TimerAssert extends MeterAssert<Timer> {

        protected TimerAssert(Timer actual) {
            super(actual);
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

        public DoubleAssert hasCount(double expected) {
            return count().isEqualTo(expected);
        }

        public DoubleAssert count() {
            return new DoubleAssert(actual.count());
        }

        private static DurationAssert toDurationAssert(Function<TimeUnit, Double> accessor) {
            double nanos = accessor.apply(TimeUnit.NANOSECONDS);
            return new DurationAssert(Duration.ofNanos((long) nanos));
        }
    }

}
