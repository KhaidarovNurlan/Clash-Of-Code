package com.server.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "users")
public class User {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  private String username;
  private String email;
  private String password;
  private String role = "student";
  private Integer points = 0;
  @Column(name = "created_at") private LocalDateTime createdAt = LocalDateTime.now();

  public User() {}
  public Long getId(){return id;}
  public String getUsername(){return username;}
  public void setUsername(String u){this.username=u;}
  public String getEmail(){return email;}
  public void setEmail(String e){this.email=e;}
  public String getPassword(){return password;}
  public void setPassword(String p){this.password=p;}
  public String getRole(){return role;}
  public void setRole(String r){this.role=r;}
  public Integer getPoints(){return points;}
  public void setPoints(Integer p){this.points=p;}
  public LocalDateTime getCreatedAt(){return createdAt;}
  public void setCreatedAt(LocalDateTime c){this.createdAt=c;}
}
