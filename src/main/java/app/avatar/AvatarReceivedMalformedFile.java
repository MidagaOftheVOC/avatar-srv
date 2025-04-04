package app.avatar;

import org.springframework.web.multipart.MultipartFile;

public class AvatarReceivedMalformedFile extends RuntimeException {
    public AvatarReceivedMalformedFile(String message) {
        super(message);
    }

    public MultipartFile f = null;
    public AvatarReceivedMalformedFile(String message, MultipartFile f){
        super(message + " Object caught in exception.");
        this.f = f;
    }
}
