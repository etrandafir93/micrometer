package io.micrometer.common.util.assertions;

import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MeterAssert<METER extends Meter> extends AbstractAssert<MeterAssert<METER>, METER> {

    protected MeterAssert(METER actual, Class<? extends MeterAssert> type) {
        super(actual, type);
    }

    public static <M extends Meter> MeterAssert<M> assertThat(M actual) {
        return new MeterAssert<>(actual, MeterAssert.class);
    }

    public ListAssert<Double> measures() {
        List<Double> measures = StreamSupport.stream(actual.measure()
                .spliterator(), false)
            .map(Measurement::getValue)
            .collect(Collectors.toList());
        return ListAssert.assertThatList(measures);
    }

    public MeterAssert<?> hasType(Meter.Type expectedType) {
        Meter.Type actualType = actual().getId()
            .getType();
        Assertions.assertThat(actualType)
            .isEqualTo(expectedType);
        return this;
    }

}
