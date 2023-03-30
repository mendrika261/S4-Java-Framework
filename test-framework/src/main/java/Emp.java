import etu2024.framework.ModelView;
import etu2024.framework.Url;

public class Emp {
    @Url(url = "/")
    public static ModelView get_all_emp() {
        return new ModelView("test.html");
    }
}
