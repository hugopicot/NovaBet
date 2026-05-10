package com.polymarket.model;

public class transactions {
    private Long id;
    private Long userId;
    private String type;
    private double amount;
    private String createdAt;
public transactions(Long id,Long userId,String type,double amount,String createdAt){
    this.id=id;
    this.userId=userId;
    this.type=type;
    this.amount=amount;
    this.createdAt=createdAt;

}
 public long getId() {
    return id;
 }
 public void setId(long id) {}
    public Long getUserId() {
    return userId;
    }
    public void setUserId(Long userId) {}
     public String getType() {
    return type;
     }
     public void setType(String type) {}
    public double getAmount() {
    return amount;
    }
    public void setAmount(double amount) {}
    public String getCreatedAt() {
    return createdAt;
    }
    public void setCreatedAt(String createdAt) {
}




}
