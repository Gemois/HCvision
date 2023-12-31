package com.hcvision.hcvisionserver.hierarchical;

import com.hcvision.hcvisionserver.dataset.Dataset;
import com.hcvision.hcvisionserver.dataset.DatasetService;
import com.hcvision.hcvisionserver.dataset.DatasetUtils;
import com.hcvision.hcvisionserver.exception.BadRequestException;
import com.hcvision.hcvisionserver.exception.NotFoundException;
import com.hcvision.hcvisionserver.hierarchical.History.HistoryService;
import com.hcvision.hcvisionserver.hierarchical.script.Optimal.Optimal;
import com.hcvision.hcvisionserver.hierarchical.script.Optimal.OptimalRequest;
import com.hcvision.hcvisionserver.hierarchical.script.Optimal.OptimalService;
import com.hcvision.hcvisionserver.hierarchical.script.PythonScript;
import com.hcvision.hcvisionserver.hierarchical.script.ResultStatus;
import com.hcvision.hcvisionserver.hierarchical.script.ScriptType;
import com.hcvision.hcvisionserver.hierarchical.script.analysis.Analysis;
import com.hcvision.hcvisionserver.hierarchical.script.analysis.AnalysisRequest;
import com.hcvision.hcvisionserver.hierarchical.script.analysis.AnalysisService;
import com.hcvision.hcvisionserver.user.User;
import com.hcvision.hcvisionserver.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class HierarchicalService {

    private final UserService userService;
    private final DatasetService datasetService;
    private final PythonExecutorService asyncPythonService;
    private final AnalysisService analysisService;
    private final OptimalService optimalService;
    private final HistoryService historyService;

    public static final String RESULT_DIR = "RESULTS";
    final String OPTIMAL_SCRIPT = "python/optimal_params.py";
    final String ANALYSIS_SCRIPT = "python/analysis.py";


    private String getPythonScriptPath(String resourcePath) {
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(Objects.requireNonNull(classLoader.getResource(resourcePath)).getFile()).getPath();
    }


    public static String getBaseResultPathByPythonScript(PythonScript pythonScript) {
        return DatasetService.getUserDirectoryPathByType(pythonScript.getDataset().getAccessType(), pythonScript.getUser()) +
                File.separator + RESULT_DIR + File.separator + pythonScript.getScriptDirName() + File.separator + pythonScript.getId();
    }


    public static String getResultPathByPythonScript(PythonScript pythonScript, String resultFilename) {
        return getBaseResultPathByPythonScript(pythonScript) + File.separator + resultFilename;
    }


    private void maybeCreateResultDirectory(PythonScript pythonScript) {
        File resultDirectory = new File(getBaseResultPathByPythonScript(pythonScript));
        if (resultDirectory.mkdirs()) {
            log.info("Result directory created for script: {}", pythonScript.getId());
        } else {
            log.error("Failed to create result directory for script: {}", pythonScript.getId());
        }
    }


    public Optimal.ProjectOptimal getOptimalParams(OptimalRequest request, String jwt) {
        User user = userService.getUserFromJwt(jwt);

        Dataset dataset = datasetService.getDataset(request.getFilename(), request.getAccessType(), user);
        if (dataset == null) throw new NotFoundException("Dataset not found");

        if (invalidParams(dataset, request.getAttributes()))
            throw new BadRequestException("Invalid attributes selected");

        List<Optimal.ProjectOptimal> reRun = optimalService.getOptimalReRun(user, dataset,
                request.isSample(), DatasetUtils.sortAttributes(request.getAttributes()));

        if (!reRun.isEmpty()) return reRun.get(0);

        Optimal optimal = optimalService.createOptimal(new Optimal(user, dataset, request.isSample(),
                DatasetUtils.sortAttributes(request.getAttributes()), ResultStatus.RUNNING));

        historyService.keepHistory(user, optimal);

        String command = "python " +
                getPythonScriptPath(OPTIMAL_SCRIPT) + " " +
                getBaseResultPathByPythonScript(optimal) + " " +
                "\"" + dataset.getPath() + "\" " +
                (request.isSample() ? "--sampling " : "") +
                DatasetUtils.encloseInDoubleQuotes(request.getAttributes()).replace(",", " ");

        maybeCreateResultDirectory(optimal);
        asyncPythonService.runScript(optimal, command);

        log.info("Python script execution started - ScriptID: {}", optimal.getId());
        return optimalService.refresh(optimal.getId());
    }


    private boolean invalidParams(Dataset dataset, String attributes) {
        String[] numericColumns = dataset.getNumericCols().split(",");
        String[] selectedAttributes = attributes.split(",");
        return !DatasetUtils.areAllElementsInArray(selectedAttributes, numericColumns);
    }


    public Analysis.ProjectAnalysis getAnalysis(AnalysisRequest request, String jwt) {
        User user = userService.getUserFromJwt(jwt);

        Dataset dataset = datasetService.getDataset(request.getFilename(), request.getAccessType(), user);
        if (dataset == null) throw new NotFoundException("Dataset not found");

        if (invalidParams(dataset,  request.getAttributes()))
            throw new BadRequestException("Invalid attributes selected");

        List<Analysis.ProjectAnalysis> reRun = analysisService.getAnalysisReRun(user, dataset, request.getLinkage(),
                request.getNumClusters(), request.isSample(), DatasetUtils.sortAttributes(request.getAttributes()));

        if (!reRun.isEmpty())
            return reRun.get(0);

        Analysis analysis = analysisService.createAnalysis(new Analysis(user, dataset, request.getLinkage(), request.getNumClusters(),
                request.isSample(), DatasetUtils.sortAttributes(request.getAttributes()), ResultStatus.RUNNING));

        historyService.keepHistory(user, analysis);

        String command = "python " +
                getPythonScriptPath(ANALYSIS_SCRIPT) + " " +
                getBaseResultPathByPythonScript(analysis) + " " +
                "\"" + dataset.getPath() + "\" " +
                request.getLinkage() + " " +
                request.getNumClusters() + " " +
                (request.isSample() ? "--sampling " : "") +
                DatasetUtils.encloseInDoubleQuotes(request.getAttributes()).replace(",", " ");

        maybeCreateResultDirectory(analysis);
        asyncPythonService.runScript(analysis, command);

        log.info("Python script execution started - ScriptID: {}", analysis.getId());
        return analysisService.refresh(analysis.getId());
    }

    public Object getScriptStatus(ScriptType scriptType, long id, String jwt) {
        User user = userService.getUserFromJwt(jwt);

        if (scriptType.equals(ScriptType.optimal)) {
            return  optimalService.getOptimalStatus(id, user)
                    .orElseThrow(() -> new  NotFoundException("No status found"));
        } else
            return analysisService.getOptimalStatus(id, user)
                    .orElseThrow(() -> new NotFoundException("No status found"));
    }
}