import etu2024.framework.annotation.Singleton;
import etu2024.framework.annotation.Url;
import etu2024.framework.core.ModelView;

@Singleton
public class Dept {
    String name;
    String path;
    byte[] content;

    @Url(url = "/reload")
    public ModelView test() {
        return new ModelView("test.jsp");
    }
}