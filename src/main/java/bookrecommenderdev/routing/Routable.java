package bookrecommenderdev.routing;

import java.util.Map;

public interface Routable {
  void onRoute(Map<String, String> params, AppContext context);
}