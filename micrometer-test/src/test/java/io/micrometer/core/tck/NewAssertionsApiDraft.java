package io.micrometer.core.tck;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static java.time.Duration.*;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

// dummy test class
// just to showcase/discuss the new assertions API

class NewAssertionsApiDraft {

    SimpleMeterRegistry simpleMeterRegistry = new SimpleMeterRegistry();

    MeterRegistryAssert meterRegistryAssert = new MeterRegistryAssert(simpleMeterRegistry);

    @Test
    void mixedExamples() {
        Counter.builder("foo")
            .tag("tag-1", "aa")
            .register(simpleMeterRegistry)
            .increment(10.0);

        meterRegistryAssert
            .counter("foo", Tag.of("tag-1", "aa"))
            .hasCount(10.0);


        Timer t = Timer.builder("bar")
            .register(simpleMeterRegistry);

        t.record(100, MILLISECONDS);
        t.record(200, MILLISECONDS);
        t.record(300, MILLISECONDS);

        meterRegistryAssert
            .meter("bar")
            .measures()
            .containsExactly(3.0, 0.6, 0.3); // count, totalTime, max

        meterRegistryAssert
            .timer("bar")
            .totalTime()
            .isLessThan(ofSeconds(1));
    }

    @Test
    @DisplayName("meterRegistryAssert -> meterAssert")
    void meterAssertions() {
        var dummy = Counter.builder("foo")
            .tag("tag-1", "aa")
            .register(this.simpleMeterRegistry);

        dummy.increment();
        dummy.increment();
        dummy.increment();

        meterRegistryAssert.meter("foo", Tag.of("tag-1", "aa"))
            //   .meter("foo", Tags.of("tag-1", "aa", "tag-2", "bb")) // <-- also possible
            .isEqualTo(dummy);

        meterRegistryAssert.meter("foo")
            .measures() // <- returns assertj's ListAssert<Double>
            .containsExactly(3.0);

        // meterRegistryAssert -> meterAssert -> CounterAssert
        meterRegistryAssert.meter("foo")
            .counter();
    }

    @Test
    @DisplayName("meterRegistryAssert -> counterAssert")
    void counterAssertions() {
        var dummy = Counter.builder("foo")
            .tag("tag-1", "aa")
            .register(this.simpleMeterRegistry);

        dummy.increment();
        dummy.increment();
        dummy.increment();

        // meterRegistryAssert -> meterAssert -> counterAssert
        meterRegistryAssert.meter("foo")
            .counter()
            .count() // <-  returns assertj's DoubleAssert,

            .isBetween(1.0, 4.0)
            .isLessThanOrEqualTo(10.0)
            .isEqualTo(3.0);
        // ... etc. (many other options offered by assertj's DoubleAssert)

        // even simpler version: meterRegistryAssert -> counterAssert
        meterRegistryAssert.counter("foo")
            .hasCount(3.0);
    }

    @Test
    @DisplayName("meterRegistryAssert -> timerAssert")
    void timerAssertions() {
        var dummy = Timer.builder("foo")
            .tag("tag-1", "aa")
            .register(this.simpleMeterRegistry);

        dummy.record(1, MILLISECONDS);
        dummy.record(100, MILLISECONDS);
        dummy.record(200, MILLISECONDS);
        dummy.record(300, MILLISECONDS);

        // meterRegistryAssert -> meterAssert -> timerAssert
        meterRegistryAssert.meter("foo")
            .timer()
            .totalTime() // <-  returns assertj's DurationAssert,

            .isGreaterThanOrEqualTo(ofMillis(600))
            .isCloseTo(ofMillis(600), ofMillis(50))
            .isEqualByComparingTo(ofMillis(601));
        //    .isBetween(...)
        // ... etc. (many other options offered by assertj's DurationAssert)

        meterRegistryAssert.timer("foo")
            .max() // .mean() / .totalTime()
            .isEqualTo(ofMillis(300));
    }

}
