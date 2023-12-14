package projet.dev_web_advanced.Generation_image;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImageController {

    @Autowired
    private ImageDAO dao = new ImageDAO();
    @Autowired
    private UserDAO userDAO = new UserDAO();
    @Autowired
    private CollectionDAO collection_DAO = new CollectionDAO();

    @PostMapping("/api/image/getImage")
    public ResponseEntity<Image> getImage(@RequestBody Long id) {
        Image i = dao.getImage(id);

        if (i != null) {
            return ResponseEntity.ok(i);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/api/image/setImage")
    public ResponseEntity<Image> setImage(@RequestBody Image i) {
        dao.modifyImage(i);
        Image imageUpdated = dao.getImage(i.getId());
        if (imageUpdated != null) {
            return ResponseEntity.ok(imageUpdated);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping(value = "/api/image/addImageToCollection")
    public ResponseEntity<String> addImageToCollection(@RequestBody Long Id_collection, Long Id_image) {
        Image i = dao.getImage(Id_image);
        Collection c = collection_DAO.getCollection(Id_collection);
        List<Image> list_image = c.getList_images();
        int length = list_image.size();
        list_image.add(i);
        c.setList_images(list_image);
        collection_DAO.modifyCollection(c);
        if (length == list_image.size() - 1) {
            return ResponseEntity.ok("Image added");
        } else {
            return ResponseEntity.status(500).body("An error occured");
        }
    }

    @PostMapping(value = "/api/image/removeImageFromCollection")
    public ResponseEntity<String> removeImageFromCollection(@RequestBody Long Id_collection, Long Id_image) {
        Image i = dao.getImage(Id_image);
        Collection c = collection_DAO.getCollection(Id_collection);
        List<Image> list_image = c.getList_images();
        int length = list_image.size();
        list_image.remove(i);
        c.setList_images(list_image);
        collection_DAO.modifyCollection(c);
        if (length == list_image.size() + 1) {
            return ResponseEntity.ok("Image removed");
        } else {
            return ResponseEntity.status(500).body("An error occured");
        }
    }
    

    public record FormulaireEnvoie(String userID, String instruction,
            ArrayList<String> selectedButtons, Number imageWidth, Number imageHeight, String seed,
            Number generationSteps, Number guidanceScale) {
    }

    @PostMapping(value = "/api/image/generate")
    public ResponseEntity<List<Image>> generateImages(@RequestBody FormulaireEnvoie formulaireEnvoi) {

        JsonObject requestGson = new JsonObject();

        requestGson.addProperty("key", "Qun1A18qioEOi9p6QmEqkEENwPwePQbXnBibjSG5ujyACdyaiEYn4BZ0Dzdr");
        requestGson.addProperty("model_id", "base-model");
        requestGson.addProperty("prompt", formulaireEnvoi.instruction);
        requestGson.addProperty("negative_prompt", "");
        requestGson.addProperty("width", formulaireEnvoi.imageWidth.toString());
        requestGson.addProperty("height", formulaireEnvoi.imageHeight.toString());
        requestGson.addProperty("samples", "4");
        requestGson.addProperty("num_inference_steps", formulaireEnvoi.generationSteps.toString());
        requestGson.addProperty("safety_checker", "no");
        requestGson.addProperty("enhance_prompt", "yes");
        if (formulaireEnvoi.seed == null) {
            requestGson.add("seed", null);
        } else {
            requestGson.addProperty("seed", formulaireEnvoi.seed);
        }
        requestGson.addProperty("guidance_scale", formulaireEnvoi.guidanceScale);
        requestGson.addProperty("multi_lingual", "no");
        requestGson.addProperty("panorama", "no");
        requestGson.addProperty("self_attention", "no");
        requestGson.addProperty("upscale", "no");
        requestGson.add("embeddings_model", null);
        requestGson.addProperty("lora_model", "japanese-style");
        requestGson.addProperty("tomesd", "yes");
        requestGson.addProperty("clip_skip", "2");
        requestGson.addProperty("use_karras_sigmas", "yes");
        requestGson.add("vae", null);
        requestGson.add("lora_strength", null);
        requestGson.addProperty("scheduler", "UniPCMultistepScheduler");
        requestGson.add("webhook", null);
        requestGson.add("track_id", null);

        OkHttpClient client = new OkHttpClient().newBuilder()
                .readTimeout(120, TimeUnit.SECONDS)
                .build();

        okhttp3.RequestBody body = okhttp3.RequestBody.create(requestGson.toString(),
                MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url("https://stablediffusionapi.com/api/v4/dreambooth")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();

        try {
            Response response = client.newCall(request).execute();
            String resp = response.body().string();
            JsonObject responseJson = JsonParser.parseString(resp).getAsJsonObject();

            List<Image> images = new ArrayList<Image>();

            JsonArray respUrls;
            String status = responseJson.get("status").toString();
            if (status.contains("processing")) {
                respUrls = responseJson.getAsJsonArray("future_links");
            } else if (status.contains("success")) {
                respUrls = responseJson.getAsJsonArray("output");
            } else {
                throw (new Exception("Error in API call"));
            }

            for (JsonElement respUrl : respUrls) {
                Image newImage = new Image();
                newImage.setCreator(userDAO.getUser(Long.parseLong(formulaireEnvoi.userID)));
                newImage.setPrompt(formulaireEnvoi.instruction);
                newImage.setNegative_prompt("");
                newImage.setModel("");
                newImage.setSeed(formulaireEnvoi.seed);
                newImage.setCfg_scale(newImage.getCfg_scale());
                newImage.setUrl_image(respUrl.toString().replace("\\", ""));
                newImage.setNote(null);
                newImage.setHeight(formulaireEnvoi.imageHeight.intValue());
                newImage.setWidth(formulaireEnvoi.imageWidth.intValue());
                newImage.setVisible(true);
                dao.createImage(newImage);
                images.add(newImage);
            }
            System.out.println(images);
            return ResponseEntity.ok(images);

        } catch (

        Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }

    }
}
