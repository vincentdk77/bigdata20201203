import java.io.Serializable;
import java.util.List;

/**
 * Created by JarvisKwok
 * Date :2020/10/16
 * Description :
 */
public class Category implements Serializable {
    private String id;
    private String pId;
    private String name;
    private List<Category> parent;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Category> getParent() {
        return parent;
    }

    public void setParent(List<Category> parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":\"" + id + "\"" +
                ",\"pId\":\"" + pId + "\"" +
                ",\"name\":\"" + name + "\"" +
                ",\"parent\":" + parent +
                '}';
    }
}
