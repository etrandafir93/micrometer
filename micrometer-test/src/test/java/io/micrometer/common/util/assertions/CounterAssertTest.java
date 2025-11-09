package io.micrometer.common.util.assertions;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.core.tck.MeterRegistryAssert;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CounterAssertTest {

    SimpleMeterRegistry simpleMeterRegistry = new SimpleMeterRegistry();

    MeterRegistryAssert meterRegistryAssert = MeterRegistryAssert.assertThat(simpleMeterRegistry);

    @Test
    void shouldFindCounterByName() {
        Counter.builder("foo")
            .register(simpleMeterRegistry)
            .increment();

        meterRegistryAssert.counter("foo")
            .hasCount(1);
    }

    @Test
    void shouldThrowIfCounterNotFound() {
        Counter.builder("foo")
            .register(simpleMeterRegistry)
            .increment();

        assertThatThrownBy(() -> meterRegistryAssert.counter("foo", Tag.of("other-tag", "xxx"))).isInstanceOf(AssertionError.class)
            .hasStackTraceContaining("Meter with name <foo> and tags <[tag(other-tag=xxx)]>")
            .hasMessageContaining("Expecting actual not to be null");
    }

    @Test
    void shouldFindCounterByNameAndTags() {
        Counter.builder("foo")
            .tag("tag-1", "aa")
            .register(simpleMeterRegistry)
            .increment(1.0);

        Counter.builder("foo")
            .tag("tag-1", "bb")
            .tag("tag-2", "cc")
            .register(simpleMeterRegistry)
            .increment(99.0);

        meterRegistryAssert.counter("foo", Tag.of("tag-1", "aa"))
            .hasCount(1);
        meterRegistryAssert.counter("foo", Tag.of("tag-1", "bb"), Tag.of("tag-2", "cc"))
            .hasCount(99);
    }

    @Test
    void shouldLeverageIntegerAsserts() {
        Counter.builder("foo")
            .register(simpleMeterRegistry)
            .increment(50.0);

        meterRegistryAssert.counter("foo")
            .count()
            .isBetween(40, 60);
    }

}
