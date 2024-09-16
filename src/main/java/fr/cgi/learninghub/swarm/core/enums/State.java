package fr.cgi.learninghub.swarm.core.enums;

import java.util.Arrays;

public enum State {
  SCHEDULED("SCHEDULED"),
  IN_PROGRESS("IN_PROGRESS"),
  DEPLOYED("DEPLOYED"),
  DEPLOYMENT_IN_ERROR("DEPLOYMENT_IN_ERROR"),
  DELETION_SCHEDULED("DELETION_SCHEDULED"),
  DELETION_IN_PROGRESS("DELETION_IN_PROGRESS"),
  DELETION_IN_ERROR("DELETION_IN_ERROR"),
  RESET_SCHEDULED("RESET_SCHEDULED"),
  RESET_IN_PROGRESS("RESET_IN_PROGRESS"),
  RESET_IN_ERROR("RESET_IN_ERROR"),
  DEACTIVATION_SCHEDULED("DEACTIVATION_SCHEDULED"),
  DEACTIVATION_IN_ERROR("DEACTIVATION_IN_ERROR"),
  DISABLED("DISABLED"),
  REACTIVATION_SCHEDULED("REACTIVATION_SCHEDULED"),
  REACTIVATION_IN_ERROR("REACTIVATION_IN_ERROR");

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