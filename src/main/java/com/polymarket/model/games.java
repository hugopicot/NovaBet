package com.polymarket.model;

public class games {
    private Long id;
    private String name;
    private String type;

    public games(Long id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public games(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public games() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "games{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
