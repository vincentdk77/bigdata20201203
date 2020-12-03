import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JarvisKwok
 * Date :2020/10/22
 * Description :
 */
public class Recursion {
    public static void main(String[] args) {

        List<Category> oldList = new ArrayList<Category>();

        try {
            File file = new File("./src/main/scala/com/kemai/recommend/category.json");
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = "";

            while ((line = br.readLine()) != null) {
                JSONObject category = JSONObject.parseObject(line);
                String id = category.getString("id");
                String pId = category.getString("pId");
                String name = category.getString("name");

                Category item = new Category();
                item.setId(id);
                item.setpId(pId);
                item.setName(name);
                oldList.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Category item : oldList) {
            item.setParent(getParent(item.getpId(), oldList));
        }

        try {
            File file = new File("./src/main/scala/com/kemai/recommend/category_dict.json");
            FileWriter wr = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(wr);
            for (int i = 0; i < oldList.size(); i++) {
                bw.write(oldList.get(i).toString()+"\r\n");
            }
            bw.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static List<Category> getParent(String id, List<Category> oldList) {
        List<Category> parentList = new ArrayList<Category>();
        for (Category item : oldList) {
            if (item.getId().equals(id)) {
                parentList.add(item);
            }
        }
        for (Category item : parentList) {
            //递归
            item.setParent(getParent(item.getpId(), oldList));
        }
        if (parentList.size() == 0) {
            return null;
        }
        return parentList;
    }
}
