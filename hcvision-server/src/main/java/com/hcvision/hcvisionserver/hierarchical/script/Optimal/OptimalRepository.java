package com.hcvision.hcvisionserver.hierarchical.script.Optimal;

import com.hcvision.hcvisionserver.dataset.Dataset;
import com.hcvision.hcvisionserver.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OptimalRepository extends JpaRepository<Optimal, Long> {
    @Override
    Optional<Optimal> findById(Long id);

    Optimal.ProjectOptimal getOptimalById(Long id);

    @Override
    List<Optimal> findAll();

    Optional<Optimal.ProjectOptimal> findByDatasetAndUserAndMaxClustersAndSampleAndAttributes(Dataset dataset, User user, int maxClusters, boolean sample, String attributes);

}
