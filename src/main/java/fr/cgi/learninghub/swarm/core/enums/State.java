package fr.cgi.learninghub.swarm.core.enums;

import java.util.Arrays;

public enum State {
  SCHEDULED("SCHEDULED"),
  IN_PROGRESS("IN_PROGRESS"),
  DEPLOYED("DEPLOYED"),
  DELETION_SCHEDULED("DELETION_SCHEDULED"),
  DELETION_IN_PROGRESS("DELETION_IN_PROGRESS"),
  RESET_SCHEDULED("RESET_SCHEDULED"),
  RESET_IN_PROGRESS("RESET_IN_PROGRESS"),
  DEACTIVATION_SCHEDULED("DEACTIVATION_SCHEDULED"),
  DISABLED("DISABLED"),
  REACTIVATION_SCHEDULED("REACTIVATION_SCHEDULED");

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