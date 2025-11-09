package io.micrometer.common.util.assertions;

import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tag;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Assertion methods for {@link Meter}s.
 * <p>
 * This class can be used for all meter types that don't have a dedicated assertion implementation.
 * For specific meter types like <em>Counters</em>, <em>Timers</em>, or <em>Gauges</em>,
 * prefer using the dedicated assertion classes:
 * {@link CounterAssert}, {@link TimerAssert}, or {@link GaugeAssert}.
 * <p>
 * To create a new instance of this class, invoke
 * {@link MeterAssert#assertThat(Meter)} or use
 * {@link io.micrometer.core.tck.MeterRegistryAssert#meter(String, Tag...)}.
 *
 * @author Emanuel Trandafir
 * @see CounterAssert
 * @see TimerAssert
 * @see GaugeAssert
 */
public class MeterAssert<METER extends Meter> extends AbstractAssert<MeterAssert<METER>, METER> {

    /**
     * Creates a new instance of {@link MeterAssert}.
     *
     * @param actual the meter to assert on
     * @param type   the class type for the assertion
     */
    protected MeterAssert(METER actual, Class<? extends MeterAssert> type) {
        super(actual, type);
    }

    /**
     * Creates a new instance of {@link MeterAssert}.
     *
     * @param actual the meter to assert on
     * @param <M>    the meter type
     * @return the created assertion object
     */
    public static <M extends Meter> MeterAssert<M> assertThat(M actual) {
        return new MeterAssert<>(actual, MeterAssert.class);
    }

    /**
     * Returns AssertJ's {@link ListAssert} for the meter's measurements.
     * <p>
     * Example:
     * <pre><code class='java'>
     * Meter meter = registry.counter("my.counter");
     *
     * assertThat(meter).measures()
     *     .isNotEmpty()
     *     .hasSize(1);
     * </code></pre>
     *
     * @return a {@link ListAssert} of measurement values for further assertions
     */
    public ListAssert<Double> measures() {
        List<Double> measures = StreamSupport.stream(actual.measure()
                .spliterator(), false)
            .map(Measurement::getValue)
            .collect(Collectors.toList());
        return ListAssert.assertThatList(measures);
    }

    /**
     * Verifies that the meter has the expected type.
     * <p>
     * Example:
     * <pre><code class='java'>
     * Meter meter = registry.counter("my.counter");
     *
     * assertThat(meter).hasType(Meter.Type.COUNTER);
     * </code></pre>
     *
     * @param expectedType the expected meter type
     * @return this assertion object for chaining
     */
    public MeterAssert<?> hasType(Meter.Type expectedType) {
        Meter.Type actualType = actual().getId()
            .getType();
        Assertions.assertThat(actualType)
            .isEqualTo(expectedType);
        return this;
    }

}
