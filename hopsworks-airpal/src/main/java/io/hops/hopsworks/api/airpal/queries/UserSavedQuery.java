package io.hops.hopsworks.api.airpal.queries;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hubspot.rosetta.annotations.StoredAsJson;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
//import lombok.EqualsAndHashCode;
import lombok.Data;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSavedQuery implements SavedQuery {

  @NotNull
  @JsonProperty
  @StoredAsJson
  private FeaturedQuery.QueryWithPlaceholders queryWithPlaceholders;

  @NotNull
  @JsonProperty
  private String user;

  @NotNull
  @JsonProperty
  private String name;

  @NotNull
  @JsonProperty
  private String description;

  @NotNull
  private DateTime createdAt;

  @NotNull
  @JsonProperty
  private UUID uuid;

  @NotNull
  @JsonProperty
  private boolean featured = false;

  @JsonProperty
  public String getCreatedAt() {
    if (createdAt != null) {
      return createdAt.toDateTimeISO().toString();
    } else {
      return null;
    }
  }
}
