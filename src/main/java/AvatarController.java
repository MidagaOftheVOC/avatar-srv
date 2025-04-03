
import avatar.service.AvatarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


/*
*   Short documentation:
*
*   1.  Accessing localhost:8081/{id}
*       Returns: A JSON with string containing a URL like "localhost:8081/images/default-avatar.png" or of format "localhost:8081/images/avatar/%s",
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
    @GetMapping("/{id}")
    public ResponseEntity<String> retrieveAvatarImageURL(@PathVariable int id){
        String avatarUrl = theAvatarService.retrieveFilenameById(id);
        return ResponseEntity.ok(avatarUrl);
    }


}
