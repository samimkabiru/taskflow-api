package com.theninjadev.taskflowapi.dtos.websocket;

public record BoardEvent<T>(String eventType, T payload) {}