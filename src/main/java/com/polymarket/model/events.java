package com.polymarket.model;

public class events {
    private Long id;
    private String title;
    private String description;
    private String status;
    private String resolution;
    private String createdAt;

public events(Long id ,String title,String description,String status,String resolution,String createdAt){
    this.id=id;
    this.title=title;
    this.description=description;
    this.status=status;
    this.resolution=resolution;
    this.createdAt=createdAt;
}
public Long getId() {
    return id;
}
public void setId(Long id) {

}
public String getTitle() {
    return title;
}
    public void setTitle(String title) {

}
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {}
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {}
    public String getResolution() {
    return resolution;

    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }




}
