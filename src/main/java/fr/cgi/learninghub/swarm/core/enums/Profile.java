package fr.cgi.learninghub.swarm.core.enums;

import java.util.Arrays;

public enum Profile {
  STUDENT("Student"),
  TEACHER("Teacher"),
  RELATIVE("Relative");

  private final String value;

  Profile(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }

  public static Profile getProfile(String value) {
     return Arrays.stream(Profile.values())
             .filter(profile -> profile.getValue().equals(value))
             .findFirst()
             .orElse(null);
  }
}