package com.example.library.entity;

public enum InventoryStatus {
    AVAILABLE("在庫"),
    BORROWED("出借中"),
    PROCESSING("整理中"),
    LOST("遺失"),
    DAMAGED("損毀"),
    DISCARDED("廢棄");

    private final String description;

    InventoryStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
