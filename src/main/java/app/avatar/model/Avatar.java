package app.avatar.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * IMPORTANT:
 *
 * @field imageFilename - is the file name of the image in the common app.avatar directory.
 * The API should handle only transmission of the filenames,
 * monolith should build the appropriate URL by combining the absolute path
 * to this microservice, the path to the app.avatar dir and teh file name in the String field.
 * Point is to not be able to find usernames by trying to access random strings.
 * Only UUID.toString() + file extension will be the names.
 *
 *  //  Note to the comment: it'd be better if this is done in the MS and
 *      the finalised URL is returned to the monolith.
 */
@Entity
@Table(name = "avatar_mappings")
public class Avatar {

    @Id //@GeneratedValue(strategy = GenerationType.UUID)   // This is more like a big map, not auto generated.
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "image_filename", nullable = false)
    private String imageFilename;

}
