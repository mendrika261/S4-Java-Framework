package etu2024.framework.utility;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
  private final DateTimeFormatter formatter;

  public LocalDateTimeAdapter() {
    formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS");
  }

  @Override
  public void write(JsonWriter out, LocalDateTime value) throws IOException {
    if (value != null) {
      out.value(formatter.format(value));
    } else {
      out.nullValue();
    }
  }

  @Override
  public LocalDateTime read(JsonReader in) throws IOException {
    String value = in.nextString();
    if (value != null) {
      return LocalDateTime.parse(value, formatter);
    } else {
      return null;
    }
  }
}
