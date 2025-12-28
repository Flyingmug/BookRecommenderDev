package bookrecommenderdev.routing.route;

import bookrecommenderdev.routing.AppContext;

import java.util.Map;

public interface Routable {
  void onRoute(Map<String, String> params, AppContext context, Object state);
}