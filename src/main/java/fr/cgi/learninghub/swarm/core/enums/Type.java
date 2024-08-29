package fr.cgi.learninghub.swarm.core.enums;

import java.util.Arrays;

public enum Type {
  WORDPRESS("WORDPRESS"),
  PRESTASHOP("PRESTASHOP");

  private final String value;

  Type(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }

  public static Type getType(String value) {
     return Arrays.stream(Type.values())
             .filter(serviceType -> serviceType.getValue().equals(value))
             .findFirst()
             .orElse(null);
  }
}