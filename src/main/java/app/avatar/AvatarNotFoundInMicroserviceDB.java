package app.avatar;
public class AvatarNotFoundInMicroserviceDB extends RuntimeException{
    public AvatarNotFoundInMicroserviceDB(String _msg){
        super(_msg);
    }
}
