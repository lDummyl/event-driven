package com.example.eventdriven.web;

/** Flat, reflection-free view of a log entry for the template. */
public record EventView(
        String type,
        String kind,
        String correlationId,
        String occurredAt,
        String payload) {
}
