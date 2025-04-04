package app;

import app.avatar.service.AvatarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


/*
*   Short documentation:
*
*   1.  Accessing localhost:8081/{id}
*       Returns: A JSON with string containing a URL like "localhost:8081/images/default-app.avatar.png" or of format "localhost:8081/images/app.avatar/%s",
*       where %s is a file name with an image
*
*   2.  Accessing localhost:8081/create_avatar
*       POST request, this includes a JSON with a mutli-part file inside
*
 */


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
     *
     */
    @GetMapping("/avatar/{id}")
    public ResponseEntity<String> retrieveAvatarImageURL(@PathVariable UUID id){
        String avatarUrl = theAvatarService.retrieveFilepathById(id);
        System.out.println("Nice: " + id);
        return ResponseEntity.ok(avatarUrl);    // yes we always return OK, if no avatar is found we return default
    }



    @GetMapping("/avatar_test")
    public ResponseEntity<Void> test(){
        System.out.println("Test successful");
        return ResponseEntity.ok().build();
    }
}
