package io.hops.hopsworks.api.airpal.presto.hive;

import com.facebook.presto.client.ClientTypeSignature;
//import com.facebook.presto.client.ClientTypeSignatureParameter;
import com.facebook.presto.client.Column;
import com.facebook.presto.spi.type.TypeSignature;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.concurrent.Immutable;

@Immutable
public class HiveColumn extends Column {

  private final boolean isPartition;
  private final boolean isNullable;

  @JsonCreator
  public HiveColumn(@JsonProperty("name") String name,
      @JsonProperty("type") String type,
      @JsonProperty("isPartition") boolean isPartition,
      @JsonProperty("isNullable") boolean isNullable) {
    super(name, type, new ClientTypeSignature(TypeSignature.parseTypeSignature(type)));
    this.isPartition = isPartition;
    this.isNullable = isNullable;
  }

  @JsonProperty
  public boolean isPartition() {
    return isPartition;
  }

  @JsonProperty
  public boolean isNullable() {
    return isNullable;
  }

  public static HiveColumn fromColumn(Column column, boolean isNullable, boolean isPartition) {
    return new HiveColumn(column.getName(), column.getType(), isPartition, isNullable);
  }
}
