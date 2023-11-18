package com.hcvision.hcvisionserver.dataset;

import com.hcvision.hcvisionserver.dataset.dto.AccessType;
import com.hcvision.hcvisionserver.dataset.dto.UploadDatasetRequest;
import com.hcvision.hcvisionserver.exception.BadRequestException;
import com.hcvision.hcvisionserver.exception.ForbiddenException;
import com.hcvision.hcvisionserver.exception.InternalServerErrorException;
import com.hcvision.hcvisionserver.exception.NotFoundException;
import com.hcvision.hcvisionserver.user.User;
import com.hcvision.hcvisionserver.user.UserService;
import lombok.AllArgsConstructor;
import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@AllArgsConstructor
public class DatasetService {

    private static final Logger logger = LoggerFactory.getLogger(DatasetService.class);

    private static final String CWD = System.getProperty("user.dir");
    private static final String DATASETS_DIRECTORY = "DATASETS";

    private final UserService userService;
    private final DatasetRepository datasetRepository;

    private static String getFilePathByUserIdAndType(String fileName, AccessType accessType, User user) {
        return CWD + File.separator + DATASETS_DIRECTORY + File.separator + accessType +
                (accessType.equals(AccessType.PRIVATE) ? File.separator + user.getId() : "") + File.separator + fileName;
    }

    public static String getUserDirectoryPathByType(AccessType accessType, User user) {
        return CWD + File.separator + DATASETS_DIRECTORY + File.separator + accessType +
                (accessType.equals(AccessType.PRIVATE) ? File.separator + user.getId() : "");
    }

    private static String getAccessTypePath(AccessType accessType) {
        return CWD + File.separator + DATASETS_DIRECTORY + File.separator + accessType;
    }

    public String saveDataset(UploadDatasetRequest request, String jwt) {

        if (request.getFile() == null || request.getAccess_type() == null)
            throw new BadRequestException("Provide both dataset file and type parameters.");

        if (!DatasetUtils.isValidFileFormat(request.getFile()))
            throw new BadRequestException("File format not supported.");

        User user = userService.getUserFromJwt(jwt);
        String fileName = request.getFile().getOriginalFilename();


        if (fileExists(fileName, request.getAccess_type(), user))
            throw new BadRequestException("File with the same name already exists.");

        maybeCreateUserDirectory(request.getAccess_type(), user);

        try {
            String filePath = getFilePathByUserIdAndType(request.getFile().getOriginalFilename(), request.getAccess_type(), user);
            request.getFile().transferTo(new File(filePath));
            Dataset dataset = new Dataset(user, fileName, request.getAccess_type(), filePath, DatasetUtils.getNumericColumns(filePath));
            datasetRepository.save(dataset);
            logger.info("Dataset saved successfully - User: {}, FileName: {}", user.getId(), fileName);
            return msg("File uploaded successfully.");
        } catch (Exception e) {
            logger.error("Error saving dataset - Error: {}", e.getMessage());
            throw new InternalServerErrorException("File was not uploaded due to internal error.");
        }
    }

    public UrlResource getDatasetFile(String fileName, AccessType accessType, String jwt) {
        User user = userService.getUserFromJwt(jwt);

        try {
            Path filePath = Paths.get(getDataset(fileName, accessType, user).getPath());
            UrlResource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                logger.info("Dataset file retrieved successfully - User: {}, FileName: {}", user.getId(), fileName);
                return resource;
            } else {
                logger.warn("Dataset file does not exist or is not readable - User: {}, FileName: {}", user.getId(), fileName);
                throw new NotFoundException("Dataset does not exist");
            }
        } catch (IOException e) {
            logger.error("Error getting dataset file - User: {}, FileName: {}. Error: {}", user.getId(), fileName, e.getMessage());
            throw new InternalServerErrorException("Something went wrong");
        }
    }

    public String deleteDataset(String fileName, AccessType accessType, String jwt) {
        User user = userService.getUserFromJwt(jwt);
        Dataset dataset = getDataset(fileName, accessType, user);

        if (dataset == null) throw new NotFoundException("Dataset does not exist");

        if (!dataset.getUser().equals(user))
            throw new ForbiddenException("You dont have permission to delete this dataset.");

        File file = new File(dataset.getPath());
        try {
            if (file.exists() && file.isFile()) {
                if (file.delete()) {
                    datasetRepository.delete(dataset);
                    logger.info("Dataset deleted - User: {}, FileName: {}", user.getId(), fileName);
                    return msg("Dataset deleted");
                } else throw new IllegalStateException("dummy");
            }
            throw new IllegalStateException("dummy");
        } catch (Exception e) {
            logger.error("Error deleting dataset - User: {}, FileName: {}. Error: {}", user.getId(), fileName, e.getMessage());
            throw new InternalServerErrorException("Error deleting dataset");
        }
    }

    private void maybeCreateUserDirectory(AccessType accessType, User user) {
        if (!userDirectoryExists(accessType, user)) {
            File accessTypeDir = new File(getAccessTypePath(accessType) +
                    (accessType.equals(AccessType.PRIVATE) ? File.separator + user.getId() : File.separator));
            if (accessTypeDir.mkdirs()) {
                logger.info("User directory created for user {}", user.getId());
            } else {
                logger.error("Failed to create user directory for user {}", user.getId());
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

    public List<Dataset.ProjectNameAndAccessType> getDatasetList(String jwt) {
        User user = userService.getUserFromJwt(jwt);
        return datasetRepository.findAllByUser(user);
    }

    public String getDataset(String fileName, AccessType accessType, String jwt) {
        User user = userService.getUserFromJwt(jwt);

        Dataset dataset = getDataset(fileName, accessType, user);
        if (dataset == null) throw new NotFoundException("Dataset does not exist");

        JSONArray jsonDataset = DatasetUtils.convertDatasetToJson(dataset.getPath());
        if (jsonDataset == null)
            throw new InternalServerErrorException("There was an error while processing the file.");

        String response = DatasetUtils.mergeJsonStrings(jsonDataset, dataset.getNumericCols());
        if (response == null) throw new InternalServerErrorException("There was an error while processing the file.");

        logger.info("Conversion and merging successful - File: {}", dataset.getPath());
        return response;
    }

    public String msg(String msg) {
        return "{\"success_msg\": \"" + msg + "\"}";
    }
}
