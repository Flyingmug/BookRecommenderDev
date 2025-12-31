package bookrecommenderdev.client.routing.route;

import bookrecommenderdev.client.routing.AppContext;

import java.util.Map;

public interface Routable {
  void onRoute(Map<String, String> params, AppContext context, Object state);
}