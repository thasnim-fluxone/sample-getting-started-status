package io.openliberty.sample;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api")   // <- all REST endpoints start with /api
public class RestApplication extends Application {
    // no code needed here
}
