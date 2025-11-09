package io.micrometer.common.util.assertions;

import io.micrometer.core.instrument.Gauge;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.DoubleAssert;

public class GaugeAssert extends AbstractAssert<GaugeAssert, Gauge> {

    public static GaugeAssert assertThat(Gauge actual) {
        return new GaugeAssert(actual);
    }

    public GaugeAssert(Gauge actual) {
        super(actual, GaugeAssert.class);
    }

    public DoubleAssert hasValue(double expected) {
        return value().isEqualTo(expected);
    }

    public DoubleAssert value() {
        return new DoubleAssert(actual.value());
    }
}
