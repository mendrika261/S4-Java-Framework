import etu2024.framework.ModelView;
import etu2024.framework.Url;

import java.sql.Date;

public class Emp {
    String name;
    Date creation;

    @Url(url = "/")
    public ModelView get_all_emp() {
        ModelView modelView = new ModelView("test.jsp");
        modelView.addItem("name", getCreation());
        return modelView;
    }

    @Url(url = "/find")
    public ModelView get(double id) {
        ModelView modelView = new ModelView("test.jsp");
        modelView.addItem("name", id);
        return modelView;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Date getCreation() {
        return creation;
    }

    public void setCreation(Date creation) {
        this.creation = creation;
    }
}
