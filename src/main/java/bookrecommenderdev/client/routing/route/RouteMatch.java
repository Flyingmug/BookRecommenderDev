package bookrecommenderdev.client.routing.route;

import java.util.Map;

/**
 * todo doc
 * */
public record RouteMatch(Route route, Map<String, String> params) {}
