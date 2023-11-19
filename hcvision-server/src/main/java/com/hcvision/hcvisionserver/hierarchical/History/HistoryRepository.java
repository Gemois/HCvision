package com.hcvision.hcvisionserver.hierarchical.History;

import com.hcvision.hcvisionserver.hierarchical.script.Optimal.Optimal;
import com.hcvision.hcvisionserver.hierarchical.script.analysis.Analysis;
import com.hcvision.hcvisionserver.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {

    Optional<History.ProjectHistory> findByUserAndId(User user, long historyId);

    Optional<History> findByIdAndUser(long historyId, User user);

    List<History.ProjectHistoryList> findAllByUser(User user);

    @Transactional
    @Modifying
    @Query("DELETE FROM History o WHERE o.optimal = ?1")
    void deleteByOptimal(Optimal optimal);


    @Transactional
    @Modifying
    @Query("DELETE FROM History o WHERE o.analysis = ?1")
    void deleteByAnalysis(Analysis analysis);

}
