package com.server.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tournament_completions", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "tournament_id"})})
public class TournamentCompletion {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "user_id") private Long userId;
    @Column(name = "tournament_id") private Long tournamentId;
    @Column(name = "completion_time") private Integer completionTime;
    @Column(name = "completed_at") private LocalDateTime completedAt = LocalDateTime.now();

    public TournamentCompletion() {}
    public TournamentCompletion(Long userId, Long tournamentId, Integer completionTime) {
        this.userId = userId;
        this.tournamentId = tournamentId;
        this.completionTime = completionTime;
        this.completedAt = LocalDateTime.now();
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getTournamentId() { return tournamentId; }
    public void setTournamentId(Long tournamentId) { this.tournamentId = tournamentId; }
    public Integer getCompletionTime() { return completionTime; }
    public void setCompletionTime(Integer completionTime) { this.completionTime = completionTime; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
