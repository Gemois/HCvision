package com.hcvision.hcvisionserver.hierarchical;

import com.hcvision.hcvisionserver.dataset.Dataset;
import com.hcvision.hcvisionserver.dataset.DatasetService;
import com.hcvision.hcvisionserver.dataset.DatasetUtils;
import com.hcvision.hcvisionserver.dataset.dto.AccessType;
import com.hcvision.hcvisionserver.hierarchical.script.Linkage;
import com.hcvision.hcvisionserver.hierarchical.script.Optimal.Optimal;
import com.hcvision.hcvisionserver.hierarchical.script.Optimal.OptimalService;
import com.hcvision.hcvisionserver.hierarchical.script.PythonScript;
import com.hcvision.hcvisionserver.hierarchical.script.ResultStatus;
import com.hcvision.hcvisionserver.hierarchical.script.analysis.Analysis;
import com.hcvision.hcvisionserver.hierarchical.script.analysis.AnalysisService;
import com.hcvision.hcvisionserver.user.User;
import com.hcvision.hcvisionserver.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class HierarchicalService {

    private final UserService userService;
    private final DatasetService datasetService;
    private final PythonExecutorService asyncPythonService;
    private final AnalysisService analysisService;
    private final OptimalService optimalService;

    final String OPTIMAL_SCRIPT = "python/optimal_params.py";
    final String ANALYSIS_SCRIPT = "python/analysis.py";

    private String getPythonScriptPath(String resourcePath) {
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(Objects.requireNonNull(classLoader.getResource(resourcePath)).getFile()).getPath();
    }

    public static final String RESULT_DIR = "results";

    public static String getBaseResultPathByPythonScript(PythonScript pythonScript) {
            return DatasetService.getUserDirectoryPathByType(pythonScript.getDataset().getAccessType(), pythonScript.getUser())
                    + File.separator + RESULT_DIR + File.separator + pythonScript.getScriptDirName() + File.separator + pythonScript.getId();

    }


    public static String getResultPathByPythonScript(PythonScript pythonScript, String resultFilename) {
        return getBaseResultPathByPythonScript(pythonScript) + File.separator + resultFilename;
    }


    private void maybeCreateResultDirectory(PythonScript pythonScript) {
            File resultDirectory = new File(getBaseResultPathByPythonScript(pythonScript));
            if (resultDirectory.mkdirs()) {
                System.out.println("Result directory created for script ");
            } else {
                System.err.println("Failed to create result directory for script ");
            }

    }


    public ResponseEntity<Optimal.ProjectOptimal> getOptimalParams(String filename, AccessType accessType, int maxClusters, String attributes, boolean isSample, String jwt) {

        User user = userService.getUserFromJwt(jwt);

        Dataset dataset = datasetService.getDataset(filename, accessType, user);

        if (dataset == null)
            return ResponseEntity.notFound().build();

        if (invalidParams(dataset, maxClusters, attributes))
            return ResponseEntity.badRequest().build();

        Optional<Optimal.ProjectOptimal> reRun = optimalService.getOptimalReRun(user, dataset , maxClusters,isSample, attributes);

        if(reRun.isPresent())
            return ResponseEntity.ok(reRun.get());

        Optimal optimal = optimalService.createOptimal(new Optimal(user, dataset, maxClusters , isSample , attributes, ResultStatus.RUNNING));

        String command = "python " +
                getPythonScriptPath(OPTIMAL_SCRIPT) + " " +
                getBaseResultPathByPythonScript(optimal) + " " +
                dataset.getPath() +  " " +
                maxClusters +  " " +
                (isSample ? "--sampling " : "") +
                attributes;


        maybeCreateResultDirectory(optimal);
        asyncPythonService.runScript(optimal, command);

        return ResponseEntity.ok(optimalService.refresh(optimal.getId()));
    }


    private boolean invalidParams(Dataset dataset, int maxClusters, String attributes) {
        String[] numericColumns = dataset.getNumericCols().split(",");
        String[] selectedAttributes = attributes.split(" ");

        return maxClusters < 0  || !DatasetUtils.areAllElementsInArray(selectedAttributes, numericColumns);
    }

    public ResponseEntity<Analysis.ProjectAnalysis> getAnalysis(String filename, AccessType accessType, Linkage linkage, int numClusters, String attributes, boolean isSample, String jwt) {
        User user = userService.getUserFromJwt(jwt);

        Dataset dataset = datasetService.getDataset(filename, accessType, user);

        if (dataset == null)
            return ResponseEntity.notFound().build();

        if (invalidParams(dataset, numClusters, attributes))
            return ResponseEntity.badRequest().build();


        Optional<Analysis.ProjectAnalysis> reRun = analysisService.getAnalysisReRun(user, dataset , linkage, numClusters, isSample, attributes);

        if(reRun.isPresent())
            return ResponseEntity.ok(reRun.get());

        Analysis analysis = analysisService.createAnalysis( new Analysis(user, dataset, linkage, numClusters , isSample , attributes, ResultStatus.RUNNING));

        String command = "python " +
                getPythonScriptPath(ANALYSIS_SCRIPT) + " " +
                getBaseResultPathByPythonScript(analysis) + " " +
                dataset.getPath() +  " " +
                linkage +  " " +
                numClusters +  " " +
                (isSample ? "--sampling " : "") +
                attributes;

        maybeCreateResultDirectory(analysis);

        asyncPythonService.runScript(analysis, command);

        return ResponseEntity.ok(analysisService.refresh(analysis.getId()));

    }

}