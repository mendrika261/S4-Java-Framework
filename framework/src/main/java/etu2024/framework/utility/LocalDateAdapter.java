package etu2024.framework.utility;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateAdapter extends TypeAdapter<LocalDate> {
  private final DateTimeFormatter formatter;

  public LocalDateAdapter() {
    formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  }

  @Override
  public void write(JsonWriter out, LocalDate localDate) throws IOException {
    if (localDate != null) {
      String formattedDate = localDate.format(formatter);
      out.value(formattedDate);
    } else {
      out.nullValue();
    }
  }

  @Override
  public LocalDate read(JsonReader in) throws IOException {
    if (in.peek() == JsonToken.NULL) {
      in.nextNull();
      return null;
    } else {
      String dateString = in.nextString();
      return LocalDate.parse(dateString, formatter);
    }
  }
}
