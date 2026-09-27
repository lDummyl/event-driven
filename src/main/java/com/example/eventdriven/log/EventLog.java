package com.example.eventdriven.log;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.eventdriven.config.LogProperties;
import com.example.eventdriven.core.DomainEvent;
import com.example.eventdriven.core.EventStore;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

/**
 * The eternal log, kept in memory for fast replay and mirrored to an append-only NDJSON file so it
 * survives restarts. It is the file-backed implementation of the {@link EventStore} port.
 */
@Component
public class EventLog implements EventStore {

    private final Path path;
    private final EventCodec codec;
    private final List<DomainEvent> events = new ArrayList<>();

    private final boolean wipeOnStart;

    public EventLog(LogProperties properties, EventCodec codec) {
        this.path = Paths.get(properties.getPath());
        this.codec = codec;
        this.wipeOnStart = properties.isWipeOnStart();
    }

    @PostConstruct
    void load() {
        try {
            Path parent = path.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            if (wipeOnStart) {
                Files.deleteIfExists(path);
            } else if (Files.exists(path)) {
                for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
                    if (!line.isBlank()) {
                        events.add(codec.decode(line));
                    }
                }
            }
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot initialise event log at " + path, e);
        }
    }

    @Override
    public synchronized void append(DomainEvent event) {
        events.add(event);
        try {
            Files.writeString(path, codec.encode(event) + System.lineSeparator(),
                    StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot append to event log at " + path, e);
        }
    }

    @Override
    public synchronized void clear() {
        events.clear();
        try {
            Files.deleteIfExists(path);
            Files.createFile(path);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot clear event log at " + path, e);
        }
    }

    @Override
    public synchronized List<DomainEvent> all() {
        return List.copyOf(events);
    }

    @Override
    public synchronized List<DomainEvent> byAggregate(String aggregateId) {
        return events.stream()
                .filter(event -> aggregateId.equals(event.aggregateId()))
                .toList();
    }

    @Override
    public synchronized int size() {
        return events.size();
    }

    @Override
    public synchronized <T extends DomainEvent> Optional<T> findResponse(UUID correlationId, Class<T> type) {
        return events.stream()
                .filter(event -> event.correlationId().equals(correlationId))
                .filter(type::isInstance)
                .map(type::cast)
                .findFirst();
    }
}
