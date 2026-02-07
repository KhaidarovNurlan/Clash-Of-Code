package com.server.service;

import com.server.model.*;
import com.server.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TournamentService {
    @Autowired private TournamentRepository tournamentRepository;
    @Autowired private TournamentLevelRepository levelRepository;
    @Autowired private TournamentLanguageRepository languageRepository;
    @Autowired private TournamentSubmissionRepository submissionRepository;
    @Autowired private TournamentCompletionRepository completionRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CodeRunnerService codeRunnerService;

    public Map<String,Object> listTournaments(int limit, int offset) {
        List<Tournament> list = tournamentRepository.findAllWithStats(limit, offset);
        int total = tournamentRepository.countAll();
        return Map.of("total", total, "tournaments", list);
    }

    public Map<String, Object> getTournamentDetails(Long id, Long userId) {
        Tournament t = tournamentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        List<Map<String, Object>> levels = levelRepository.findByTournamentIdOrderByLevelNumber(id)
                .stream()
                .map(l -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", l.getId());
                    map.put("level_number", l.getLevelNumber());
                    map.put("expected_output", l.getExpectedOutput());
                    map.put("required_keywords", l.getRequiredKeywords());
                    map.put("points", l.getPoints());
                    return map;
                })
                .toList();

        List<Map<String, Object>> languages = languageRepository.findByTournamentId(id)
                .stream()
                .map(lang -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("code", lang.getLanguageCode());
                    m.put("name", lang.getLanguageCode());
                    return m;
                })
                .toList();

        List<Map<String, Object>> leaderboard = completionRepository.findByTournamentIdOrderByCompletionTimeAsc(id)
                .stream()
                .map(c -> {
                    Map<String, Object> row = new HashMap<>();
                    User u = userRepository.findById(c.getUserId()).orElse(null);
                    row.put("user_id", c.getUserId());
                    row.put("username", u != null ? u.getUsername() : "Unknown");
                    row.put("completion_time", c.getCompletionTime());

                    long completedLevels = submissionRepository.countDistinctPassedLevels(c.getUserId(), id);
                    row.put("completed_levels", completedLevels);
                    return row;
                })
                .toList();

        Integer nextLevelNumber = 1;
        if (userId != null) {
            Integer maxPassed = submissionRepository.findMaxPassedLevelNumber(userId, id);
            if (maxPassed != null) {
                nextLevelNumber = maxPassed + 1;
            }
        }

        Map<String, Object> tournament = new HashMap<>();
        tournament.put("id", t.getId());
        tournament.put("title", t.getTitle());
        tournament.put("description", t.getDescription());
        tournament.put("difficulty", t.getDifficulty());
        tournament.put("languages", languages);
        tournament.put("levels", levels);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("tournament", tournament);
        response.put("leaderboard", leaderboard);
        response.put("nextLevelNumber", nextLevelNumber);

        return response;
    }

    public Map<String,Object> createTournament(Long teacherId, Map<String,Object> body) {
        User teacher = userRepository.findById(teacherId).orElseThrow(() -> new RuntimeException("Teacher not found"));
        String title = (String) body.get("title");
        String description = (String) body.get("description");
        String difficulty = (String) body.get("difficulty");
        Tournament t = new Tournament(title, description, difficulty, teacher);
        tournamentRepository.save(t);

        @SuppressWarnings("unchecked")
        List<String> languages = (List<String>) body.getOrDefault("languages", List.of());
        for (String lang : languages) {
            TournamentLanguage tl = new TournamentLanguage(t, lang);
            languageRepository.save(tl);
        }

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> levels = (List<Map<String,Object>>) body.getOrDefault("levels", List.of());
        for (Map<String,Object> lvl : levels) {
            Integer levelNumber = (Integer) lvl.get("level_number");
            String expectedOutput = (String) lvl.get("expected_output");
            @SuppressWarnings("unchecked")
            List<String> requiredKeywords = lvl.get("required_keywords") instanceof List ?
                    (List<String>) lvl.get("required_keywords") :
                    List.of(String.valueOf(lvl.get("required_keywords")));
            Integer points = lvl.get("points") == null ? 0 : (Integer) lvl.get("points");
            TournamentLevel level = new TournamentLevel(t, levelNumber, expectedOutput, requiredKeywords, points);
            levelRepository.save(level);
        }

        return Map.of("status","success","tournamentId", t.getId());
    }

    public Map<String,Object> submitSolution(Long userId, Long tournamentId, Long levelId, String code, String languageCode) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        TournamentLevel level = levelRepository.findById(levelId)
                .orElseThrow(() -> new RuntimeException("Level not found"));

        if (!tournamentRepository.existsById(tournamentId)) {
            throw new RuntimeException("Tournament not found");
        }

        String output = codeRunnerService.runCode(languageCode, code);

        boolean outputMatches = output != null &&
        output.replaceAll("\\r?\\n|<EOL>", "").trim()
              .equals(level.getExpectedOutput().replaceAll("\\r?\\n", "").trim());
        String[] keywords = level.getRequiredKeywords() == null ? new String[0] : level.getRequiredKeywords();

        boolean codeHasKeywords = true;
        for (String kw : keywords) {
            if (!code.contains(kw)) {
                codeHasKeywords = false;
                break;
            }
        }
        boolean passed = outputMatches && codeHasKeywords;

        TournamentSubmission submission = new TournamentSubmission(userId, tournamentId, levelId, passed);
        submissionRepository.save(submission);

        boolean tournamentCompleted = false;
        Integer nextLevelNumber = null;

        if (passed) {
            int points = level.getPoints() == null ? 0 : level.getPoints();
            user.setPoints(user.getPoints() + points);
            userRepository.save(user);

            long totalLevels = levelRepository.countByTournamentId(tournamentId);
            long completedLevels = submissionRepository.countDistinctPassedLevels(userId, tournamentId);

            if (completedLevels >= totalLevels) {
                tournamentCompleted = true;
            } else {
                List<TournamentLevel> nextLevels = levelRepository.findNextLevels(tournamentId, level.getLevelNumber());
                if (!nextLevels.isEmpty()) {
                    nextLevelNumber = nextLevels.get(0).getLevelNumber();
                }
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("passed", passed);
        response.put("output", output);
        response.put("points", passed ? level.getPoints() : 0);
        response.put("tournament_completed", tournamentCompleted);
        response.put("nextLevelNumber", nextLevelNumber);

        return response;
    }

    public Map<String,Object> completeTournament(Long userId, Long tournamentId, Integer completionTime) {
        if (!userRepository.existsById(userId)) throw new RuntimeException("User not found");
        if (!tournamentRepository.existsById(tournamentId)) throw new RuntimeException("Tournament not found");

        TournamentCompletion c = new TournamentCompletion(userId, tournamentId, completionTime);
        completionRepository.save(c);
        return Map.of("status","success");
    }
}
