package io.micrometer.common.util.assertions;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.core.tck.MeterRegistryAssert;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GaugeAssertTest {

    SimpleMeterRegistry simpleMeterRegistry = new SimpleMeterRegistry();

    MeterRegistryAssert meterRegistryAssert = MeterRegistryAssert.assertThat(simpleMeterRegistry);

    @Test
    void shouldFindGaugeByName() {
        Gauge.builder("foo", () -> 10.0).register(simpleMeterRegistry);

        meterRegistryAssert.gauge("foo").hasValue(10.0);
    }

    @Test
    void shouldThrowIfGaugeNotFound() {
        Gauge.builder("foo", () -> 10.0).register(simpleMeterRegistry);

        assertThatThrownBy(() -> meterRegistryAssert.gauge("foo", Tag.of("other-tag", "xxx")))
            .isInstanceOf(AssertionError.class)
            .hasStackTraceContaining("Meter with name <foo> and tags <[tag(other-tag=xxx)]>")
            .hasMessageContaining("Expecting actual not to be null");
    }

    @Test
    void shouldFindGaugeByNameAndTags() {
        Gauge.builder("foo", 1.0, n -> n).tag("tag-1", "aa").register(simpleMeterRegistry);

        Gauge.builder("foo", 99.0, n -> n).tag("tag-1", "bb").tag("tag-2", "cc").register(simpleMeterRegistry);

        meterRegistryAssert.gauge("foo", Tag.of("tag-1", "aa")).hasValue(1.0);
        meterRegistryAssert.gauge("foo", Tag.of("tag-1", "bb"), Tag.of("tag-2", "cc")).hasValue(99.0);
    }

    @Test
    void shouldLeverageDoubleAsserts() {
        Gauge.builder("foo", () -> 50.0).register(simpleMeterRegistry);

        meterRegistryAssert.gauge("foo").value().isBetween(40.0, 60.0);
    }

}
