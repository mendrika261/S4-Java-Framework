package etu2024.framework.utility;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalTimeAdapter extends TypeAdapter<LocalTime> {

  private final DateTimeFormatter formatter;

  public LocalTimeAdapter() {
    formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
  }

  @Override
  public void write(JsonWriter out, LocalTime localTime) throws IOException {
    if (localTime != null) {
      String formattedTime = localTime.format(formatter);
      out.value(formattedTime);
    } else {
      out.nullValue();
    }
  }

  @Override
  public LocalTime read(JsonReader in) throws IOException {
    if (in.peek() == JsonToken.NULL) {
      in.nextNull();
      return null;
    } else {
      String timeString = in.nextString();
      return LocalTime.parse(timeString, formatter);
    }
  }
}
