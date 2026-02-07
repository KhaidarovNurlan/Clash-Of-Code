package com.server.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;

@Entity
@Table(name="tournament_languages")
public class TournamentLanguage {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="tournament_id") @JsonBackReference private Tournament tournament;
  @Column(name="language_code") private String languageCode;

  public TournamentLanguage() {}
  public TournamentLanguage(Tournament t, String lang) {
    this.tournament=t;
    this.languageCode=lang;
  }
  public Long getId() { return id; }
  public Tournament getTournament() { return tournament; }
  public void setTournament(Tournament tournament) { this.tournament = tournament; }
  public String getLanguageCode() { return languageCode; }
  public void setLanguageCode(String languageCode) { this.languageCode = languageCode; }
}
