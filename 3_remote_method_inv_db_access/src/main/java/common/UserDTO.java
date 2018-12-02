package common;

import server.model.File;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public interface UserDTO extends Serializable {

    public int getId();

    public String getUsername();

    public String getPassword();

    public List<File> getFiles();

    public Date getCreatedAt();

    public Date getUpdatedAt();

}
