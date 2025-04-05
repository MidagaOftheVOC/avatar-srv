package app.avatar.service;

import app.Application;
import app.avatar.AvatarNotFoundInMicroserviceDB;
import app.avatar.AvatarReceivedMalformedFile;
import app.avatar.AvatarReceivedWrongFileFormat;
import app.avatar.AvatarUserAlreadyHasAvatar;
import app.avatar.model.Avatar;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import app.avatar.repo.AvatarRepository;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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

    public String[] returnAllusers(){
        UUID[] allIds = theAvatarRepo.findAllIds();
        String[] self = new String[allIds.length];

        for(int i = 0; i < allIds.length; i++){
            self[i] = allIds[i].toString();
        }

        return self;
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

        Path intendedAbsolutePath = getPathToAvatarStorage().resolve(newFilename);

        System.out.println("Resolved path: " + intendedAbsolutePath.toString());

        //  create directory tree up to satisfy requirements
        //  in this case, it creates only ~/avatar-srv/avatar-storage/
        Files.createDirectories(intendedAbsolutePath.getParent());

        //  this statement should save the file
        file.transferTo(intendedAbsolutePath.toFile());

        Avatar avatar = new Avatar();

        avatar.setId(userId);
        avatar.setImageFilename(newFilename);

        theAvatarRepo.save(avatar);
    }

    public void deleteAvatar(UUID userId) throws IOException {

        Optional<Avatar> avatar = theAvatarRepo.findById(userId);

        if(!avatar.isPresent()){
            throw new AvatarNotFoundInMicroserviceDB("Trying to delete avatar of user who has no avatar.");
        }

        File fileToRemove =  getPathToAvatarStorage().resolve(avatar.get().getImageFilename()).toFile();
        if(!fileToRemove.delete()){
            System.out.println("Couldn't delete file at location: " + fileToRemove.getAbsolutePath());
            throw new IOException("Couldn't delete file at location: " + fileToRemove.getAbsolutePath());
        }

        theAvatarRepo.delete(avatar.get());
    }

    private Path getPathToAvatarStorage(){
        return FileSystems.getDefault().getPath("").toAbsolutePath()
                .resolve(avatarImageStorage);
    }

    /**
     * @return Returns a random file name WITHOUT an extention.
     */
    public String generateFileName(){
        String newFilename = UUID.randomUUID().toString();
        return newFilename;
    }

    public String retrieveFilepathById(UUID id){
        Optional<Avatar> avatar = theAvatarRepo.findById(id);
        if(!avatar.isPresent()){
            return baseURL + "images/default-avatar.png";
        }
        return baseURL + "avatar-storage/" + avatar.get().getImageFilename();
    }
}
