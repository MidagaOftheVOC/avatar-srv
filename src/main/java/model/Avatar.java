package model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "avatar_mappings")
/**
 * IMPORTANT:
 *
 * @field imageFilename - is the file name of the image in the common avatar directory.
 * The API should handle only transmission of the filenames,
 * monolith should build the appropriate URL by combining the absolute path
 * to this microservice, the path to the avatar dir and teh file name in the String field.
 *
 *  //  Note to the comment: it'd be better if this is done in the MS and
 *      the finalised URL is returned to the monolith.
 */
public class Avatar {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "image_filename", nullable = false)
    private String imageFilename;

}
