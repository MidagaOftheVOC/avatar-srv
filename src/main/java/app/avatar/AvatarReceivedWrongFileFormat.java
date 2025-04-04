package app.avatar;

import org.springframework.web.multipart.MultipartFile;

public class AvatarReceivedWrongFileFormat extends RuntimeException {
    public AvatarReceivedWrongFileFormat(String message) {
        super(message);
    }

    MultipartFile f;
    public AvatarReceivedWrongFileFormat(String message, MultipartFile f){
        super(message + " Object caught in exception.");
        this.f = f;
    }
}
