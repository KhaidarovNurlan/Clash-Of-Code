package com.server.model;

import jakarta.persistence.*;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name="tournaments")
public class Tournament {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  private String title;
  private String description;
  private String difficulty;
  @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="created_by") private User createdBy;
  @OneToMany(mappedBy="tournament", cascade=CascadeType.ALL) @JsonManagedReference private List<TournamentLanguage> languages;
  @OneToMany(mappedBy="tournament", cascade=CascadeType.ALL) @JsonManagedReference private List<TournamentLevel> levels;


  public Tournament() {}
  public Tournament(String title, String description, String difficulty, User createdBy) {
    this.title=title;
    this.description=description;
    this.difficulty=difficulty;
    this.createdBy=createdBy;
  }
  public Long getId() { return id; }
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public String getDifficulty() { return difficulty; }
  public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
  public User getCreatedBy() { return createdBy; }
  public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
  public List<TournamentLanguage> getLanguages() { return languages; }
  public void setLanguages(List<TournamentLanguage> languages) { this.languages = languages; }
  public List<TournamentLevel> getLevels() { return levels; }
  public void setLevels(List<TournamentLevel> levels) { this.levels = levels; }
}
