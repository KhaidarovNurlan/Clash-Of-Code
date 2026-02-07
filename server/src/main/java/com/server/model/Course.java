package com.server.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="courses")
public class Course {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String title;
  private String description;
  private String level;
  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="created_by") private User createdBy;
  @OneToMany(mappedBy="course", cascade=CascadeType.ALL, orphanRemoval=true) private List<Lesson> lessons;
  
  public Course() {}
  public Long getId() { return id; }
  public void setTitle(String t) { this.title=t; }
  public String getTitle() { return title; }
  public void setDescription(String d) { this.description=d; }
  public String getDescription() { return description; }
  public void setLevel(String l) { this.level=l; }
  public String getLevel() { return level; }
  public User getCreatedBy() { return createdBy; }
  public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
}
