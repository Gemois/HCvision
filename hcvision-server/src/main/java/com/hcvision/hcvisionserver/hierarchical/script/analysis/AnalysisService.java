package com.hcvision.hcvisionserver.hierarchical.script.analysis;

import com.hcvision.hcvisionserver.dataset.Dataset;
import com.hcvision.hcvisionserver.hierarchical.HierarchicalService;
import com.hcvision.hcvisionserver.hierarchical.script.Linkage;
import com.hcvision.hcvisionserver.hierarchical.script.ResultStatus;
import com.hcvision.hcvisionserver.user.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AnalysisService {

    private final AnalysisRepository analysisRepository;

    public void saveResults(Analysis analysis) {
        analysis.setParallelCoordinatesResultPath(HierarchicalService.getResultPathByPythonScript(analysis, Analysis.getParallelCoordinatesResultFileName()));
        analysis.setClusterAssignmentResultPath(HierarchicalService.getResultPathByPythonScript(analysis, Analysis.getClusterAssignmentResultFileName()));
        analysis.setDendrogramResultPath(HierarchicalService.getResultPathByPythonScript(analysis, Analysis.getDendrogramResultFileName()));
        analysis.setEndedAt(LocalDateTime.now());
        analysis.calcDuration();
        analysis.setStatus(ResultStatus.FINISHED);
        analysisRepository.save(analysis);
    }


    public void informError(Analysis analysis) {
        analysis.setStatus(ResultStatus.ERROR);
        analysis.setEndedAt(LocalDateTime.now());
        analysisRepository.save(analysis);
    }

    public Analysis createAnalysis(Analysis analysis) { return analysisRepository.save(analysis); }

    public Optional<Analysis.ProjectAnalysis> getAnalysisReRun(User user, Dataset dataset, Linkage linkage, int numClusters, boolean isSample, String attributes) {
        return analysisRepository.findByDatasetAndUserAndLinkageAndNumClustersAndSampleAndAttributes(dataset, user, linkage, numClusters, isSample, attributes);
    }


    public Optional<Analysis.ProjectAnalysisStatus> getOptimalStatus(long id, User user) {
        return analysisRepository.getStatus(id, user);
    }

    public Analysis.ProjectAnalysis refresh(long id) { return analysisRepository.getAnalysisById(id); }

}
