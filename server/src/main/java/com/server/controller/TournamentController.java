package com.server.controller;

import com.server.service.AuthService;
import com.server.service.TournamentService;
import com.server.repository.ProgrammingLanguageRepository;
import com.server.model.ProgrammingLanguage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tournaments")
public class TournamentController {

    @Autowired private AuthService authService;
    @Autowired private TournamentService tournamentService;
    @Autowired private ProgrammingLanguageRepository programmingLanguageRepository;

    @GetMapping
    public ResponseEntity<?> listTournaments(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return ResponseEntity.ok(tournamentService.listTournaments(limit, offset));
    }

    @PostMapping
    public ResponseEntity<?> createTournament(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> body) {
        Long teacherId = authService.getUserIdFromAuthHeader(authHeader);
        return ResponseEntity.ok(tournamentService.createTournament(teacherId, body));
    }

    @GetMapping("/languages")
    public ResponseEntity<?> getLanguages() {
        try {
            List<ProgrammingLanguage> languages = programmingLanguageRepository.findAllByOrderByNameAsc();
            return ResponseEntity.ok(languages);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/{tournamentId}/submit")
    public ResponseEntity<?> submitSolution(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long tournamentId,
            @RequestBody Map<String, Object> body) {
        Long userId = authService.getUserIdFromAuthHeader(authHeader);
        Long levelId = ((Number) body.get("level_id")).longValue();
        String code = (String) body.get("code");
        String languageCode = (String) body.get("languageCode");
        return ResponseEntity.ok(
            tournamentService.submitSolution(userId, tournamentId, levelId, code, languageCode)
        );
    }

    @PostMapping("/{tournamentId}/complete")
    public ResponseEntity<?> completeTournament(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long tournamentId,
            @RequestBody Map<String, Object> body) {
        Long userId = authService.getUserIdFromAuthHeader(authHeader);
        Integer completionTime = body.get("completion_time") == null ? 0 :
                Integer.parseInt(String.valueOf(body.get("completion_time")));
        return ResponseEntity.ok(tournamentService.completeTournament(userId, tournamentId, completionTime));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDetails(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        Long userId = authService.getUserIdFromAuthHeader(authHeader);
        return ResponseEntity.ok(tournamentService.getTournamentDetails(id, userId));
    }
}
