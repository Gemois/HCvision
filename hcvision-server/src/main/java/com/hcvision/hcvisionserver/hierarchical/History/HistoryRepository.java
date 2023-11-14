package com.hcvision.hcvisionserver.hierarchical.History;

import com.hcvision.hcvisionserver.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistoryRepository extends JpaRepository<History,Long> {

    Optional<History> findByIdAndUser(long historyId, User user);

    List<History> findAllByUser(User user);
}