package io.micrometer.common.util.assertions;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.core.tck.MeterRegistryAssert;
import org.junit.jupiter.api.Test;

class MeterAssertTest {

    SimpleMeterRegistry simpleMeterRegistry = new SimpleMeterRegistry();

    MeterRegistryAssert meterRegistryAssert = MeterRegistryAssert.assertThat(simpleMeterRegistry);

    @Test
    void shouldAssertOnMeasures() {
        Counter.builder("foo")
            .register(simpleMeterRegistry)
            .increment(10.0);

        meterRegistryAssert.meter("foo")
            .measures()
            .containsExactly(10.0);
    }


    @Test
    void shouldAssertOnType() {
        Counter.builder("foo")
            .register(simpleMeterRegistry)
            .increment(10.0);

        meterRegistryAssert.meter("foo")
            .hasType(Meter.Type.COUNTER);
    }

}
