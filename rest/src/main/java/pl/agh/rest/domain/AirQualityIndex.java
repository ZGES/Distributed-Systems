package pl.agh.rest.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableAirQualityIndex.class)
public interface AirQualityIndex {

    StIndexLevel stIndexLevel();
}
