package service;

import model.Avatar;
import org.springframework.stereotype.Service;
import repo.AvatarRepository;

import java.util.UUID;

@Service
public class AvatarService {

    final String RELATIVE_PATH_TO_IMAGES_DIR = "/resources/images";
    final String RELATIVE_PATH_TO_AVATAR_DIR = "/resources/images/avatars";

    final private AvatarRepository theAvatarRepo;

    public AvatarService(
            AvatarRepository avatarRepository
    )
    {
        theAvatarRepo = avatarRepository;
    }

    public String generateFileName(String username){
        String finalResult;

        finalResult = username + "_";
        finalResult += UUID.randomUUID().toString();

        return finalResult;
    }



}
