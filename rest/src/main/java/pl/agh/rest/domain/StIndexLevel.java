package pl.agh.rest.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableStIndexLevel.class)
public interface StIndexLevel {

    String indexLevelName();
}
