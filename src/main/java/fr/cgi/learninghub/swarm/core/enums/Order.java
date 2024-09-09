package fr.cgi.learninghub.swarm.core.enums;

import io.quarkus.panache.common.Sort.Direction;

import java.util.Arrays;

public enum Order {
  ASCENDANT("ASC"),
  DESCENDANT("DESC");

  private final String value;

  Order(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }

  public static Order getOrder(String value) {
    return Arrays.stream(Order.values())
            .filter(order -> order.getValue().equals(value))
            .findFirst()
            .orElse(null);
  }

  public Direction getDirection() {
    return ASCENDANT.getValue().equals(this.value) ? Direction.Ascending : Direction.Descending;
  }
}