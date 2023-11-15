package com.hcvision.hcvisionserver.hierarchical.History;

import com.hcvision.hcvisionserver.user.User;
import com.hcvision.hcvisionserver.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class HistoryService {

    private final UserService userService;
    private final HistoryRepository historyRepository;

    public ResponseEntity<History.ProjectHistory> findHistoryById(long historyId, String jwt) {
        User user = userService.getUserFromJwt(jwt);

        Optional<History.ProjectHistory> history = historyRepository.findByUserAndId(user, historyId);

        return history.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

    }

    public void deleteHistory(long historyId, String jwt) {

        User user = userService.getUserFromJwt(jwt);

        Optional<History> history = historyRepository.findByIdAndUser(historyId, user);

        history.ifPresent(historyRepository::delete);

    }

    public List<History.ProjectHistoryList> getAllHistory(String jwt) {

        User user = userService.getUserFromJwt(jwt);

        return historyRepository.findAllByUser(user);

    }

}
