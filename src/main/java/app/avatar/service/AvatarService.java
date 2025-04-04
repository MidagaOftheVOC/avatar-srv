package app.avatar.service;

import app.Application;
import app.avatar.AvatarReceivedMalformedFile;
import app.avatar.AvatarReceivedWrongFileFormat;
import app.avatar.AvatarUserAlreadyHasAvatar;
import app.avatar.model.Avatar;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import app.avatar.repo.AvatarRepository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
public class AvatarService {

    final String RELATIVE_PATH_TO_DEFAULT_AVATAR = "resources/images/default-app.avatar.png";

    final String[] ALLOWED_FILE_EXTENTIONS = {
            "png",
            "gif",
            "jpg",
            "jpeg"
    };

    // real smart
    @Value("${app.avatar.base-url}")
    private String baseURL;

    @Value("${app.avatar.storage-dir}")
    private String avatarImageStorage;

    final private AvatarRepository theAvatarRepo;

    public AvatarService(
            AvatarRepository avatarRepository
    )
    {
        theAvatarRepo = avatarRepository;
    }

    private String getFileExtension(String fullFileName){
        return fullFileName.substring(fullFileName.indexOf('.') + 1);
    }

    /**
     *  Note:
     *          Technically, return code of this function is irrelevant.
     *          Its sole purpose is to capture all validation inside,
     *          so there shouldn't be checks outside which would still
     *          just throw exceptions either way.
     *
     *  Potential optimisation:
     *          Just return the extension from here to save a call to getFileExtension();
     */
    private boolean validateFile(MultipartFile file){

        if(file.getOriginalFilename() == null){
            throw new AvatarReceivedMalformedFile("MultipartFile::getOriginalFilename() returned null.", file);
        }

        int index = file.getOriginalFilename().indexOf('.');

        if(index == -1 || index == file.getOriginalFilename().length()){
            throw new AvatarReceivedMalformedFile("File name has no extension.", file);
        }

        boolean allowedExtenion = false;
        String extension = file.getOriginalFilename().substring(index);

        for(int i = 0; i < ALLOWED_FILE_EXTENTIONS.length; i ++){
            if(extension.toLowerCase().contains(ALLOWED_FILE_EXTENTIONS[i])){
                allowedExtenion = true;
                break;
            }
        }

        if(!allowedExtenion){
            throw new AvatarReceivedWrongFileFormat("File has an unsupported extension/format.", file);
        }

        return true;
    }

    public void saveAvatarImageFile(UUID userId, MultipartFile file)
            throws IOException, AvatarReceivedMalformedFile, AvatarUserAlreadyHasAvatar, AvatarReceivedWrongFileFormat
    {

        Optional<Avatar> existing = theAvatarRepo.findById(userId);
        if (existing.isPresent()) {
            throw new AvatarUserAlreadyHasAvatar("In POST request: User with ID [%s] already has an avatar.".formatted(userId));
        }

        validateFile(file);

        String extention = getFileExtension(file.getOriginalFilename());
        String newFilename  = generateFileName() + "." + extention;

        Path intendedAbsolutePath = FileSystems.getDefault().getPath("").toAbsolutePath()
                .resolve(avatarImageStorage)
                .resolve(newFilename);


        System.out.println("Resolved path: " + intendedAbsolutePath.toString());

        //  create directory tree up to satisfy requirements
        //  in this case, it creates only ~/avatar-srv/avatar-storage/
        Files.createDirectories(intendedAbsolutePath.getParent());

        //  this statement should save the file
        file.transferTo(intendedAbsolutePath.toFile());

        Avatar avatar = new Avatar();

        avatar.setId(userId);
        avatar.setImageFilename(intendedAbsolutePath.toString());

        theAvatarRepo.save(avatar);
    }

    /**
     * @return Returns a random file name WITHOUT an extention.
     */
    public String generateFileName(){
        String newFilename = UUID.randomUUID().toString();
        return newFilename;
    }

    public String retrieveFilepathById(UUID id){

        // Yes, nulls should be allowed, otherwise part of Avatar management will fall on monolith
        Avatar avatar = theAvatarRepo.findById(id).orElseGet(() -> null);

        if(avatar == null){
            return prependUrlCommonlements(RELATIVE_PATH_TO_DEFAULT_AVATAR);
        }

        return prependUrlCommonlements(avatarImageStorage + avatar.getImageFilename());
    }

    private String prependUrlCommonlements(String relativeToCurrentHostURL){
        return baseURL + relativeToCurrentHostURL;
    }

}
