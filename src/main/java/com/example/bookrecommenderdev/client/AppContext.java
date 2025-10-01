package com.example.bookrecommenderdev.client;

import com.example.bookrecommenderdev.model.Utente;
import com.example.bookrecommenderdev.server.ServerInterface;

public record AppContext(ServerInterface server, Utente user) { }
