package io.micrometer.common.util.assertions;

import io.micrometer.core.instrument.Counter;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.IntegerAssert;

public class CounterAssert extends AbstractAssert<CounterAssert, Counter> {

    public static CounterAssert assertThat(Counter actual) {
        return new CounterAssert(actual);
    }

    public CounterAssert(Counter actual) {
        super(actual, CounterAssert.class);
    }

    public IntegerAssert hasCount(int expected) {
        return count().isEqualTo(expected);
    }

    public IntegerAssert count() {
        return new IntegerAssert((int) actual.count());
    }
}
