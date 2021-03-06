package io.github.cottonmc.cottontweaks;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.property.AbstractProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import net.minecraft.util.registry.Registry;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A block state property for fluids.
 */
public final class FluidProperty extends AbstractProperty<FluidProperty.Wrapper> {
    public static final Wrapper EMPTY = new Wrapper(() -> Fluids.EMPTY);
    public static final Wrapper WATER = new Wrapper(() -> Fluids.WATER);
    public static final Wrapper LAVA = new Wrapper(() -> Fluids.LAVA);
    private static final Set<Wrapper> VANILLA_FLUID_SET = Sets.newHashSet(FluidProperty.WATER, FluidProperty.LAVA, FluidProperty.EMPTY);
    public static final FluidProperty VANILLA_FLUIDS = new FluidProperty("fluid", () -> VANILLA_FLUID_SET);
    public static final FluidProperty ANY_FLUID = new FluidProperty("fluid");

    private final Supplier<Collection<Wrapper>> fluids;

    public FluidProperty(String var1, Supplier<Collection<Wrapper>> fluids) {
        super(var1, Wrapper.class);
        this.fluids = fluids;
    }

    public FluidProperty(String var1) {
        this(var1, () -> Registry.FLUID.stream().map(Wrapper::new).collect(Collectors.toList()));
    }

    @Override
    public Collection<Wrapper> getValues() {
        return fluids.get();
    }

    @Override
    public Optional<Wrapper> getValue(String str) {
        if (Strings.isNullOrEmpty(str))
            throw new IllegalArgumentException("str must not be empty or null!");

        try {
            Identifier id = new Identifier(str);
            if (Registry.FLUID.containsId(id)) {
                Wrapper fluid = new Wrapper(Registry.FLUID.get(id));
                if (getValues().contains(fluid))
                    return Optional.of(fluid);
            }
        } catch (Exception ignored) {}

        return Optional.empty();
    }

    @Override
    public String getName(Wrapper var1) {
        // Requires StateFactoryMixin
        Identifier id = Registry.FLUID.getId(var1.get());
        return id.toString();
    }

    @Override
    public boolean equals(Object other) {
        return this == other;
    }

    public static final class Wrapper extends Lazy<Fluid> implements Comparable<Wrapper> {
        public Wrapper(Fluid fluid) {
            this(() -> fluid);
        }

        public Wrapper(Supplier<Fluid> fluid) {
            super(fluid);
        }

        public Fluid getFluid() {
            return get();
        }

        @Override
        public int compareTo(Wrapper o) {
            return Integer.compare(getId(get()), getId(o.get()));
        }

        private static int getId(Fluid fluid) {
            return fluid != null ? Registry.FLUID.getRawId(fluid) : -1;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(get());
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Wrapper && Objects.equals(((Wrapper) obj).get(), get());
        }

        @Override
        public String toString() {
            return Registry.FLUID.getId(get()).toString();
        }
    }
}
