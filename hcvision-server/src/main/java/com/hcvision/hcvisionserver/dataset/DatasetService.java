package com.hcvision.hcvisionserver.dataset;

import com.hcvision.hcvisionserver.dataset.dto.AccessType;
import com.hcvision.hcvisionserver.dataset.dto.UploadDatasetRequest;
import com.hcvision.hcvisionserver.user.User;
import com.hcvision.hcvisionserver.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@AllArgsConstructor
public class DatasetService {

    private static final String CWD = System.getProperty("user.dir");
    private static final String DATASETS_DIRECTORY = "DATASETS";

    private final UserService userService;
    private final DatasetRepository datasetRepository;

    private static String getFilePathByUserIdAndType(String fileName, AccessType accessType, User user) {
        return CWD + File.separator + DATASETS_DIRECTORY + File.separator + accessType + (accessType.equals(AccessType.PRIVATE) ? File.separator + user.getId() : "") + File.separator + fileName;
    }

    public static String getUserDirectoryPathByType(AccessType accessType, User user) {
        return CWD + File.separator + DATASETS_DIRECTORY + File.separator + accessType + (accessType.equals(AccessType.PRIVATE) ? File.separator + user.getId() : "");
    }

    private static String getAccessTypePath(AccessType accessType) {
        return CWD + File.separator + DATASETS_DIRECTORY + File.separator + accessType;
    }

    public ResponseEntity<String> saveFile(UploadDatasetRequest uploadDatasetRequest, String jwt) {

        if (!DatasetUtils.isValidFileFormat(uploadDatasetRequest.getFile()))
            return ResponseEntity.badRequest().body("File format not supported.");

        User user = userService.getUserFromJwt(jwt);
        String fileName = uploadDatasetRequest.getFile().getOriginalFilename();

        if (!(uploadDatasetRequest.getType().equals(AccessType.PUBLIC) || uploadDatasetRequest.getType().equals(AccessType.PRIVATE)))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Access type not recognised.");

        if (fileExists(fileName, uploadDatasetRequest.getType(), user))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File with the same name already exists.");

        maybeCreateUserDirectory(uploadDatasetRequest.getType(), user);

        try {
            String filePath = getFilePathByUserIdAndType(uploadDatasetRequest.getFile().getOriginalFilename(), uploadDatasetRequest.getType(), user);
            uploadDatasetRequest.getFile().transferTo(new File(filePath));
            Dataset dataset = new Dataset(user, fileName, uploadDatasetRequest.getType(), filePath, DatasetUtils.getNumericColumns(filePath));
            datasetRepository.save(dataset);
            return ResponseEntity.ok("File uploaded successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File was not uploaded due to internal error.");
        }
    }

    public ResponseEntity<UrlResource> findFile(String fileName, AccessType accessType, String jwt) {
        User user = userService.getUserFromJwt(jwt);

        try {
            Path filePath = Paths.get(getDataset(fileName, accessType, user).getPath());
            UrlResource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName).body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<String> deleteFile(String fileName, AccessType accessType, String jwt) {
        User user = userService.getUserFromJwt(jwt);
        Dataset dataset = getDataset(fileName, accessType, user);

        if (dataset == null) return ResponseEntity.notFound().build();

        if (!dataset.getUser().equals(user))
            return ResponseEntity.badRequest().body("You dont have permission to delete this dataset.");

        File file = new File(dataset.getPath());
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                datasetRepository.delete(dataset);
                return ResponseEntity.ok().body("File deleted Successfully.");
            } else return ResponseEntity.badRequest().body("File not deleted due to internal error.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private void maybeCreateUserDirectory(AccessType accessType, User user) {
        if (!userDirectoryExists(accessType, user)) {
            File accessTypeDir = new File(getAccessTypePath(accessType) + (accessType.equals(AccessType.PRIVATE) ? File.separator + user.getId() : File.separator));
            if (accessTypeDir.mkdirs()) {
                System.out.println("User directory created for user " + user.getId());
            } else {
                System.err.println("Failed to create user directory for user " + user.getId());
            }
        }
    }

    private boolean fileExists(String file, AccessType accessType, User user) {
        File filePath = new File(getFilePathByUserIdAndType(file, accessType, user));
        return filePath.exists() && filePath.isFile();
    }

    public boolean userDirectoryExists(AccessType accessType, User user) {
        File userDirectory = new File(getUserDirectoryPathByType(accessType, user));
        return userDirectory.exists() && userDirectory.isDirectory();
    }

    public Dataset getDataset(String fileName, AccessType accessType, User user) {
        if (accessType.equals(AccessType.PRIVATE))
            return datasetRepository.findByFileNameAndAccessTypeAndUser(fileName, accessType, user);
        else return datasetRepository.findByAccessTypeAndFileName(AccessType.PUBLIC, fileName);
    }

    public List<Dataset.ProjectNameAndAccessType> getDatasets(String jwt) {
        User user = userService.getUserFromJwt(jwt);
        return datasetRepository.findAllByUser(user);
    }

    public ResponseEntity<String> getDatasetInJson(String fileName, AccessType accessType, String jwt) {
        User user = userService.getUserFromJwt(jwt);
        Dataset dataset = getDataset(fileName, accessType, user);

        if (dataset == null)
            return ResponseEntity.notFound().build();

        String jsonDataset = DatasetUtils.convertDatasetToJson(dataset.getPath());

        if (jsonDataset == null)
            return ResponseEntity.internalServerError().body("There was an error while processing the file.");

        return ResponseEntity.ok().body(jsonDataset);
    }
}
