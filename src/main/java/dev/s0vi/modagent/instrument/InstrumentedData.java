package dev.s0vi.modagent.instrument;

import net.fabricmc.loader.impl.launch.knot.Knot;

import java.util.Optional;

/***
 * Data acquired through instrumentation.
 */
public class InstrumentedData {
    public static Optional<Knot> KNOT = Optional.empty();
}
