import etu2024.framework.ModelView;
import etu2024.framework.Url;

public class Emp {
    @Url(url = "/")
    public static ModelView get_all_emp() {
        ModelView modelView = new ModelView("test.jsp");
        modelView.addItem("name", "John");
        return modelView;
    }
}
