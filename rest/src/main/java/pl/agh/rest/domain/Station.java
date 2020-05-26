package pl.agh.rest.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.OptionalInt;

@Value.Immutable
@JsonDeserialize(as = ImmutableStation.class)
public interface Station {

    OptionalInt id();
    City city();
}
