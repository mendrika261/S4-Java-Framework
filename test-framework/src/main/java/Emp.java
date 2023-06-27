import etu2024.framework.core.File;
import etu2024.framework.core.ModelView;
import etu2024.framework.annotation.Url;

import java.sql.Date;
import java.util.Arrays;

public class Emp {
    String[] name;
    Date creation;
    File file;

    @Url(url = "/")
    public ModelView get_all_emp() {
        ModelView modelView = new ModelView("test.jsp");
        if(getFile() != null)
            if(getFile().isReady())
                modelView.addItem("name", getFile());
        return modelView;
    }

    @Url(url = "/find")
    public ModelView get(Double id) {
        ModelView modelView = new ModelView("test.jsp");
        if(name != null)
            modelView.addItem("name", Arrays.toString(name));
        return modelView;
    }

    public Date getCreation() {
        return creation;
    }

    public void setCreation(Date creation) {
        this.creation = creation;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
