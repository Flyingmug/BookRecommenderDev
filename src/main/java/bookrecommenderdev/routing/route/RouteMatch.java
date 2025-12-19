package bookrecommenderdev.routing.route;

import java.util.Map;

public record RouteMatch(Route route, Map<String, String> params) {}
