package io.micrometer.common.util.assertions;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Statistic;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.core.tck.MeterRegistryAssert;
import org.junit.jupiter.api.Test;

class MeterAssertTest {

    SimpleMeterRegistry simpleMeterRegistry = new SimpleMeterRegistry();

    MeterRegistryAssert meterRegistryAssert = MeterRegistryAssert.assertThat(simpleMeterRegistry);

    @Test
    void shouldAssertOnMeasures() {
        DistributionSummary meter = DistributionSummary.builder("foo").register(simpleMeterRegistry);

        meter.record(10.0);
        meter.record(20.0);

        meterRegistryAssert.meter("foo")
            .hasMeasurement(Statistic.COUNT, 2.0)
            .hasMeasurement(Statistic.TOTAL, 30.0)
            .hasMeasurement(Statistic.MAX, 20.0);
    }

    @Test
    void shouldAssertOnType() {
        DistributionSummary.builder("foo").register(simpleMeterRegistry).record(100.0);

        meterRegistryAssert.meter("foo").hasType(Meter.Type.DISTRIBUTION_SUMMARY);
    }

}
