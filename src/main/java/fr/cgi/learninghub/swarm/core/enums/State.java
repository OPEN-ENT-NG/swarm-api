package fr.cgi.learninghub.swarm.core.enums;

import java.util.Arrays;

public enum State {
  SCHEDULED("SCHEDULED"),
  IN_PROGRESS("IN_PROGRESS"),
  DEPLOYED("DEPLOYED"),
  DELETION_SCHEDULED("DELETION_SCHEDULED"),
  ON_DELETION("ON_DELETION");

  private final String value;

  State(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }

  public static State getState(String value) {
     return Arrays.stream(State.values())
             .filter(serviceState -> serviceState.getValue().equals(value))
             .findFirst()
             .orElse(null);
  }
}