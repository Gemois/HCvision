package com.hcvision.hcvisionserver.hierarchical;

import com.hcvision.hcvisionserver.hierarchical.script.analysis.Analysis;
import com.hcvision.hcvisionserver.hierarchical.script.analysis.AnalysisService;
import com.hcvision.hcvisionserver.hierarchical.script.Optimal.Optimal;
import com.hcvision.hcvisionserver.hierarchical.script.Optimal.OptimalService;
import com.hcvision.hcvisionserver.hierarchical.script.PythonScript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;

@Service
public class PythonExecutorService {

    private final ExecutorService service;
    private final OptimalService optimalService;
    private final AnalysisService analysisService;

    @Autowired
    public PythonExecutorService(@Qualifier("pythonExecutor") ExecutorService service,
                                 OptimalService optimalService,
                                 AnalysisService analysisService) {
        this.service = service;
        this.optimalService = optimalService;
        this.analysisService = analysisService;
    }

    protected void runScript(PythonScript pythonScript, String command) {
        Runnable script;

        if (pythonScript instanceof Optimal) {

            script = () -> {
                try {
                    ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
                    processBuilder.redirectErrorStream(true);
                    Process process = processBuilder.start();

                    StringBuilder output = new StringBuilder();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                    }

                    int exitCode = process.waitFor();
                    if (exitCode != 0) {
                        throw new IOException("Python script execution failed with exit code " + exitCode);
                    }
                    optimalService.saveResults((Optimal) pythonScript, output.toString());
                } catch (IOException | InterruptedException e) {
                    optimalService.informError((Optimal) pythonScript);
                }

            };

        } else {
            script = () -> {
                try {
                    ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
                    processBuilder.redirectErrorStream(true);
                    Process process = processBuilder.start();

                    int exitCode = process.waitFor();
                    if (exitCode != 0) {
                        throw new IOException("Command execution failed with exit code " + exitCode);
                    }
                    analysisService.saveResults((Analysis) pythonScript);
                } catch (InterruptedException | IOException e) {
                    analysisService.informError((Analysis) pythonScript);
                }
            };

        }
        service.submit(script);
    }
}
