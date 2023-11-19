package com.hcvision.hcvisionserver.hierarchical.History;

import com.hcvision.hcvisionserver.exception.NotFoundException;
import com.hcvision.hcvisionserver.hierarchical.script.Optimal.Optimal;
import com.hcvision.hcvisionserver.hierarchical.script.PythonScript;
import com.hcvision.hcvisionserver.hierarchical.script.analysis.Analysis;
import com.hcvision.hcvisionserver.user.User;
import com.hcvision.hcvisionserver.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class HistoryService {

    private final UserService userService;
    private final HistoryRepository historyRepository;

    public void keepHistory(User user, PythonScript script) {
        try {
            if (script instanceof Optimal)
                historyRepository.save(new History(LocalDateTime.now(), user, (Optimal) script));
            else historyRepository.save(new History(LocalDateTime.now(), user, (Analysis) script));
        } catch (Exception e) {
            log.error("Error saving history. User: {}, Script ID: {}. Error: {}", user.getId(), script.getId(), e.getMessage());
        }
    }

    public History.ProjectHistory getHistoryById(long historyId, String jwt) {
        User user = userService.getUserFromJwt(jwt);

        return historyRepository.findByUserAndId(user, historyId)
                .orElseThrow(() -> new NotFoundException("History not found"));

    }

    public String deleteHistory(long historyId, String jwt) {
        User user = userService.getUserFromJwt(jwt);

        History history = historyRepository.findByIdAndUser(historyId, user)
                .orElseThrow(() -> new NotFoundException("History not found"));

        historyRepository.delete(history);

        return msg("User deleted along with all his information.");
    }

    public List<History.ProjectHistoryList> getAllHistory(String jwt) {

        User user = userService.getUserFromJwt(jwt);

        return historyRepository.findAllByUser(user);

    }

    public String msg(String msg) { return "{\"success_msg\": \"" + msg + "\"}"; }

}
