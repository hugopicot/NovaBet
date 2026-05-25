package com.polymarket.model;

public class Game {

    private long id;
    private String name;
    private GameType type;

    public Game() {
    }

    public Game(String name, GameType type) {
        this.name = name;
        this.type = type;
    }

    public Game(long id, String name, GameType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public GameType getType() { return type; }
    public void setType(GameType type) { this.type = type; }

    @Override
    public String toString() {
        return "Game{id=" + id + ", name='" + name + "', type=" + type + '}';
    }
}
