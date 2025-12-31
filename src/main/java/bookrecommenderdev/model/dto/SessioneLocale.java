package bookrecommenderdev.model.dto;

import java.io.Serial;
import java.io.Serializable;

public record SessioneLocale(String token) implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
}