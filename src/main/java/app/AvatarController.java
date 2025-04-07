package app;

import app.avatar.AvatarReceivedMalformedFile;
import app.avatar.AvatarReceivedWrongFileFormat;
import app.avatar.AvatarUserAlreadyHasAvatar;
import app.avatar.model.Avatar;
import app.avatar.service.AvatarService;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
public class AvatarController {

    final private AvatarService theAvatarService;

    public AvatarController(
            AvatarService avatarService
    )
    {
        theAvatarService = avatarService;
    }

    /**
     *  {id} contains a value from the "users" table in the monolith, which must match
     *  an ID that maps to a file name stored in this MS' database.
     */
    @GetMapping("/avatar/{id}")
    public ResponseEntity<String> retrieveAvatarImageURL(@PathVariable UUID id){
        String avatarUrl = theAvatarService.retrieveFilepathById(id);
        System.out.println(avatarUrl);
        return ResponseEntity           // yes we always return OK, if no user-specific avatar is found we return default avatar url
                .status(HttpStatus.OK)
                .body(avatarUrl);
    }

    @PostMapping("/upload_avatar/{userId}")
    public ResponseEntity<Void> uploadAvatar(@PathVariable("userId") UUID userId,
                                             @RequestParam("file") MultipartFile file)
    {
        System.out.printf("Entered /upload_avatar with ID [%s]\n", userId);
        try{
            theAvatarService.saveAvatarImageFile(userId, file);

            // if all is good
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .build();
        }
        catch (IOException e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
        catch (AvatarReceivedMalformedFile e){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
        catch(AvatarReceivedWrongFileFormat e){
            return ResponseEntity
                    .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .build();
        }
        catch (AvatarUserAlreadyHasAvatar e){
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .build();
        }
    }

    @DeleteMapping("/delete_avatar/{userId}")
    public ResponseEntity<Void> deleteAvatar(@PathVariable("userId") UUID userId){
        try {
            theAvatarService.deleteAvatar(userId);
        } catch (IOException e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/get_storage_path")
    public ResponseEntity<String> getStoragePath(){
        return ResponseEntity
                .ok()
                .body(theAvatarService.getStoragePath());
    }

    @GetMapping("/avatar_test")
    public ResponseEntity<Void> test(){
        System.out.println("Test successful");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/return_all_users")
    public ResponseEntity<String[]> allUsers(){
        System.out.println("Printing all users");
        return ResponseEntity.ok().body(theAvatarService.returnAllusers());
    }
}
