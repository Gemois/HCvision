package com.hcvision.hcvisionserver.hierarchical.script.Optimal;

import com.hcvision.hcvisionserver.dataset.Dataset;
import com.hcvision.hcvisionserver.hierarchical.HierarchicalService;
import com.hcvision.hcvisionserver.hierarchical.script.ResultStatus;
import com.hcvision.hcvisionserver.user.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class OptimalService {

    private OptimalRepository optimalRepository;

    public void saveResults(Optimal optimal, String result) {
        optimal.setStatus(ResultStatus.FINISHED);
        optimal.setResult(HierarchicalService.getResultPathByPythonScript(optimal, optimal.getOptimalParamsResultFileName()));
        optimalRepository.save(optimal);
    }

    public void informError(Optimal optimal) {
        optimal.setStatus(ResultStatus.ERROR);
        optimalRepository.save(optimal);
    }

    public Optimal createOptimal(Optimal optimal) {
        return optimalRepository.save(optimal);
    }

    public Optional<Optimal.ProjectOptimal> getOptimalReRun(User user, Dataset dataset, int maxClusters, boolean isSample, String attributes) {
        return optimalRepository.findByDatasetAndUserAndMaxClustersAndSampleAndAttributes(dataset, user, maxClusters, isSample, attributes);
    }

    public Optimal.ProjectOptimal refresh(long id) {
        return optimalRepository.getOptimalById(id);
    }
}
