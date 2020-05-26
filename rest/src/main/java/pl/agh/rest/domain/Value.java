package pl.agh.rest.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.OptionalDouble;

@org.immutables.value.Value.Immutable
@JsonDeserialize(as = ImmutableValue.class)
public interface Value {

    OptionalDouble value();
}
