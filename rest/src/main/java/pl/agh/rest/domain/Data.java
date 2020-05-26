package pl.agh.rest.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;


@Value.Immutable
@JsonDeserialize(as = ImmutableData.class)
public interface Data {

    String key();
    pl.agh.rest.domain.Value[] values();
}
