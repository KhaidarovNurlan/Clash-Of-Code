package com.server.model;

import jakarta.persistence.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonBackReference;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "tournament_levels")
public class TournamentLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="tournament_id")
    @JsonBackReference
    private Tournament tournament;

    @Column(name = "level_number")
    private Integer levelNumber;

    @Column(name = "expected_output")
    private String expectedOutput;

    @Column(columnDefinition = "text[]")
    private String[] requiredKeywords;

    private Integer points = 0;

    public TournamentLevel() {}

    public TournamentLevel(Tournament t, Integer levelNumber, String expectedOutput, List<String> requiredKeywords, Integer points) {
        this.tournament = t;
        this.levelNumber = levelNumber;
        this.expectedOutput = expectedOutput;
        this.requiredKeywords = requiredKeywords != null
                ? requiredKeywords.toArray(new String[0])
                : new String[0];
        this.points = points;
    }

    public Long getId() { return id; }

    public Tournament getTournament() { return tournament; }
    public void setTournament(Tournament tournament) { this.tournament = tournament; }

    public Integer getLevelNumber() { return levelNumber; }
    public void setLevelNumber(Integer levelNumber) { this.levelNumber = levelNumber; }

    public String getExpectedOutput() { return expectedOutput; }
    public void setExpectedOutput(String expectedOutput) { this.expectedOutput = expectedOutput; }

    public String[] getRequiredKeywords() {
      return requiredKeywords;
    }

    public void setRequiredKeywords(String[] requiredKeywords) {
        this.requiredKeywords = requiredKeywords;
    }

    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }
}
