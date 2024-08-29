package fr.cgi.learninghub.swarm.core.enums;

import java.util.Arrays;

public enum UserServiceName {
  ENT("ENT");

  private final String value;

  UserServiceName(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }

  public static UserServiceName getUserServiceName(String value) {
    return Arrays.stream(UserServiceName.values())
      .filter(userServiceName -> userServiceName.getValue().equals(value))
      .findFirst()
      .orElse(null);
  }
}