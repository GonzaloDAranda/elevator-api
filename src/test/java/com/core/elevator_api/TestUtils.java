package com.core.elevator_api;

import com.google.gson.JsonParser;
import lombok.experimental.UtilityClass;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;

@UtilityClass
public class TestUtils {

  public static String getContentFromJsonFile(String filename) throws FileNotFoundException {
    InputStream expectedResponseInputStream = TestUtils.class.getClassLoader().getResourceAsStream(
      filename);
    if (expectedResponseInputStream == null) {
      throw new FileNotFoundException(filename + " is not found. Please check /test/resources folder.");
    }
    return JsonParser.parseReader(
      new InputStreamReader(expectedResponseInputStream)).toString();
  }

}
