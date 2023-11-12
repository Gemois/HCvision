package com.hcvision.hcvisionserver.hierarchical.script.analysis;

import com.hcvision.hcvisionserver.dataset.Dataset;
import com.hcvision.hcvisionserver.hierarchical.script.Linkage;
import com.hcvision.hcvisionserver.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface AnalysisRepository extends JpaRepository<Analysis, Long> {

    @Override
    Optional<Analysis> findById(Long id);

    Analysis.ProjectAnalysis getAnalysisById(Long id);

    Optional<Analysis.ProjectAnalysis> findByDatasetAndUserAndLinkageAndNumClustersAndSampleAndAttributes(Dataset dataset,
                                                                                                          User user,
                                                                                                          Linkage linkage,
                                                                                                          int numClusters,
                                                                                                          boolean sample,
                                                                                                          String attributes);

}
