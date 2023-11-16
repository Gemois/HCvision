package com.hcvision.hcvisionserver.hierarchical.History;

import com.hcvision.hcvisionserver.exception.NotFoundException;
import com.hcvision.hcvisionserver.hierarchical.script.Optimal.Optimal;
import com.hcvision.hcvisionserver.hierarchical.script.PythonScript;
import com.hcvision.hcvisionserver.hierarchical.script.analysis.Analysis;
import com.hcvision.hcvisionserver.user.User;
import com.hcvision.hcvisionserver.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class HistoryService {

    private final UserService userService;
    private final HistoryRepository historyRepository;

    public void keepHistory(User user, PythonScript script) {
        if (script instanceof Optimal)
            historyRepository.save(new History(LocalDateTime.now(), user, (Optimal) script));
        else
            historyRepository.save(new History(LocalDateTime.now(), user, (Analysis) script));
    }

    public History.ProjectHistory getHistoryById(long historyId, String jwt) {
        User user = userService.getUserFromJwt(jwt);

        return historyRepository.findByUserAndId(user, historyId)
                .orElseThrow(() -> new NotFoundException("History not found"));

    }

    public String  deleteHistory(long historyId, String jwt) {

        User user = userService.getUserFromJwt(jwt);

        Optional<History> history = historyRepository.findByIdAndUser(historyId, user);

        history.ifPresent(historyRepository::delete);

        return msg("User deleted along with all his information.");
    }

    public List<History.ProjectHistoryList> getAllHistory(String jwt) {

        User user = userService.getUserFromJwt(jwt);

        return historyRepository.findAllByUser(user);

    }

    public String msg(String msg) {
        return "{\"success_msg\": \"" + msg + "\"}";
    }

}
