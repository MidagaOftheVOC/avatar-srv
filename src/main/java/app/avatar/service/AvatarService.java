package app.avatar.service;

import app.avatar.model.Avatar;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import app.avatar.repo.AvatarRepository;

import java.util.UUID;

@Service
public class AvatarService {

    final String RELATIVE_PATH_TO_DEFAULT_AVATAR = "resources/images/default-app.avatar.png";
    final String RELATIVE_PATH_TO_AVATAR_DIR = "resources/images/avatars/";

    // real smart
    @Value("${app.avatar.base-url}")
    private String baseURL;

    final private AvatarRepository theAvatarRepo;

    public AvatarService(
            AvatarRepository avatarRepository
    )
    {
        theAvatarRepo = avatarRepository;
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

        return prependUrlCommonlements(RELATIVE_PATH_TO_AVATAR_DIR + avatar.getImageFilename());
    }

    private String prependUrlCommonlements(String relativeToCurrentHostURL){
        return baseURL + relativeToCurrentHostURL;
    }



}
