package com.hcvision.hcvisionserver.hierarchical.script.analysis;

import com.hcvision.hcvisionserver.dataset.Dataset;
import com.hcvision.hcvisionserver.hierarchical.script.Linkage;
import com.hcvision.hcvisionserver.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnalysisRepository extends JpaRepository<Analysis, Long> {

    Analysis.ProjectAnalysis getAnalysisById(Long id);

    List<Analysis.ProjectAnalysis> findByDatasetAndUserAndLinkageAndNumClustersAndSampleAndAttributes
            (Dataset dataset, User user, Linkage linkage, int numClusters, boolean sample, String attributes);

    @Query("SELECT o FROM Analysis o WHERE o.id = ?1 AND o.user = ?2")
    Optional<Analysis.ProjectAnalysisStatus> getStatus(Long id, User user);

    @Transactional
    @Modifying
    @Query("DELETE FROM Analysis s WHERE s.user = ?1")
    void deleteAllUserAnalysis(User user);

    @Transactional
    @Modifying
    @Query("DELETE FROM Analysis o WHERE o.dataset = ?1")
    void deleteAllDatasetAnalysis(Dataset dataset);

    List<Analysis> findByUser(User user);

    List<Analysis> findByDataset(Dataset dataset);

}
