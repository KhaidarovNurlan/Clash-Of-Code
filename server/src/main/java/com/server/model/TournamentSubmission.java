package com.server.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tournament_submissions")
public class TournamentSubmission {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "user_id") private Long userId;
    @Column(name = "tournament_id") private Long tournamentId;
    @Column(name = "level_id") private Long levelId;
    @Column(nullable = false) private Boolean passed = false;

    public TournamentSubmission() {}
    public TournamentSubmission(Long userId, Long tournamentId, Long levelId, Boolean passed) {
        this.userId = userId;
        this.tournamentId = tournamentId;
        this.levelId = levelId;
        this.passed = passed != null ? passed : false;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getTournamentId() { return tournamentId; }
    public void setTournamentId(Long tournamentId) { this.tournamentId = tournamentId; }
    public Long getLevelId() { return levelId; }
    public void setLevelId(Long levelId) { this.levelId = levelId; }
    public Boolean getPassed() { return passed; }
    public void setPassed(Boolean passed) { this.passed = passed; }
}
