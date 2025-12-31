package bookrecommenderdev.model.data;

import java.util.List;

public interface PageResult<T> {
  List<T> results();
  int totalCount();
}