import etu2024.framework.ModelView;
import etu2024.framework.Url;

public class Emp {
    String name;
    Double creation;

    @Url(url = "/")
    public ModelView get_all_emp() {
        ModelView modelView = new ModelView("test.jsp");
        modelView.addItem("name", getCreation());
        return modelView;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Double getCreation() {
        return creation;
    }

    public void setCreation(Double creation) {
        this.creation = creation;
    }
}
