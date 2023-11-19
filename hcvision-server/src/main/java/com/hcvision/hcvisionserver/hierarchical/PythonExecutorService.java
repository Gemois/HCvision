package com.hcvision.hcvisionserver.hierarchical;

import com.hcvision.hcvisionserver.hierarchical.script.analysis.Analysis;
import com.hcvision.hcvisionserver.hierarchical.script.analysis.AnalysisService;
import com.hcvision.hcvisionserver.hierarchical.script.Optimal.Optimal;
import com.hcvision.hcvisionserver.hierarchical.script.Optimal.OptimalService;
import com.hcvision.hcvisionserver.hierarchical.script.PythonScript;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;

@Service
@AllArgsConstructor
@Slf4j
public class PythonExecutorService {

    private final ExecutorService service;
    private final OptimalService optimalService;
    private final AnalysisService analysisService;

    protected void runScript(PythonScript pythonScript, String command) {
        Runnable script;

        if (pythonScript instanceof Optimal) {

            script = () -> {
                try {
                    ((Optimal) pythonScript).setStartedAt(LocalDateTime.now());
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
                        log.error("Python script execution failed for Optimal script - ScriptID: {}. Exit code: {}. Output: {}", pythonScript.getId(), exitCode, output);
                        throw new IOException("Python script execution failed with exit code " + exitCode);
                    } else {
                        log.info("Python script executed successfully for Optimal script - ScriptID: {}", pythonScript.getId());
                    }
                    optimalService.saveResults((Optimal) pythonScript);

                } catch (IOException | InterruptedException e) {
                    log.error("Error executing Python script for Optimal script - ScriptID: {}. Error: {}", pythonScript.getId(), e.getMessage());
                    optimalService.informError((Optimal) pythonScript);
                }

            };

        } else {
            script = () -> {
                try {
                    ((Analysis) pythonScript).setStartedAt(LocalDateTime.now());
                    ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
                    processBuilder.redirectErrorStream(true);
                    Process process = processBuilder.start();

                    int exitCode = process.waitFor();
                    if (exitCode != 0) {
                        log.error("Command execution failed for Analysis script - ScriptID: {}. Exit code: {}", pythonScript.getId(), exitCode);
                        throw new IOException("Command execution failed with exit code " + exitCode);
                    } else {
                        log.info("Python script executed successfully for Optimal script - ScriptID: {}", pythonScript.getId());
                    }
                    analysisService.saveResults((Analysis) pythonScript);
                } catch (InterruptedException | IOException e) {
                    log.error("Error executing command for Analysis script - ScriptID: {}. Error: {}", pythonScript.getId(), e.getMessage());
                    analysisService.informError((Analysis) pythonScript);
                }
            };

        }

        service.submit(script);
    }
}
