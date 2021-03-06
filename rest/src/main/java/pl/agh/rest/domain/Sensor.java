package pl.agh.rest.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;


@Value.Immutable
@JsonDeserialize(as = ImmutableSensor.class)
public interface Sensor {

    Integer id();
}
