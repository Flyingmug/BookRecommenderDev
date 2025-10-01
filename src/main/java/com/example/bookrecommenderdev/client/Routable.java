package com.example.bookrecommenderdev.client;

import java.util.Map;

public interface Routable {
  void onRoute(Map<String, String> params, AppContext context);
}