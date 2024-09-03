package fr.cgi.learninghub.swarm.core.enums;

import java.util.Arrays;

public enum PathType {
  WORDPRESS("wp"),
  PRESTASHOP("presta");

  private final String value;

  PathType(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }

  public static String getValue(Type type) {
    return Arrays.stream(PathType.values())
             .filter(pathType -> pathType.name().equals(type.getValue()))
             .findFirst()
             .map(pathType -> pathType.getValue())
             .orElse(null);
  }

  public static PathType getPathType(String value) {
     return Arrays.stream(PathType.values())
             .filter(pathType -> pathType.getValue().equals(value))
             .findFirst()
             .orElse(null);
  }
}