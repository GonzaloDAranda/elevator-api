package com.core.elevator_api.constants;

import com.core.elevator_api.api.exception.InvalidDataException;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum Directions {
  UP("UP"),
  DOWN("DOWN"),
  IDLE("IDLE");

  private final String value;

  Directions(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return value;
  }

  @JsonCreator
  public static Directions fromValue(String value){
    for(Directions direction : Directions.values()){
      if(direction.getValue().equals(value)){
        return direction;
      }
    }
    throw new InvalidDataException("The field direction with value: " + value + " is invalid");
  }
}
