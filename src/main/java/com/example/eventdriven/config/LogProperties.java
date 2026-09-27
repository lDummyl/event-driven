package com.example.eventdriven.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.log")
public class LogProperties {

    /** Path of the append-only NDJSON file that holds the eternal event log. */
    private String path = "./data/event-log.ndjson";

    /** When true the service wipes the whole log on startup, starting a brand new history. */
    private boolean wipeOnStart = true;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isWipeOnStart() {
        return wipeOnStart;
    }

    public void setWipeOnStart(boolean wipeOnStart) {
        this.wipeOnStart = wipeOnStart;
    }
}
