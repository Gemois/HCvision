package com.hcvision.hcvisionserver.hierarchical.script.Optimal;

import com.hcvision.hcvisionserver.dataset.Dataset;
import com.hcvision.hcvisionserver.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface OptimalRepository extends JpaRepository<Optimal, Long> {

    Optimal.ProjectOptimal getOptimalById(Long id);

    Optional<Optimal.ProjectOptimal> findByDatasetAndUserAndMaxClustersAndSampleAndAttributes
            (Dataset dataset, User user, int maxClusters, boolean sample, String attributes);

    @Query("SELECT o FROM Optimal o WHERE o.id = ?1 AND o.user = ?2")
    Optional<Optimal.ProjectOptimalStatus> getStatus(Long id, User user);

    @Transactional
    @Modifying
    @Query("DELETE FROM Optimal o WHERE o.user = ?1")
    void deleteAllUserOptimal(User user);

    @Transactional
    @Modifying
    @Query("DELETE FROM Optimal o WHERE o.dataset = ?1")
    void deleteAllDatasetOptimal(Dataset dataset);


    List<Optimal> findByUser(User user);

    List<Optimal> findByDataset(Dataset dataset);
}
