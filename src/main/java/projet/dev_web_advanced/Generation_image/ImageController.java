package projet.dev_web_advanced.Generation_image;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.google.gson.Gson;

public class ImageController {

    private ImageDAO dao;
    private UserDAO userDAO;
    private CollectionDAO collection_DAO;

    @PostMapping("/api/image/getImage")
    public ResponseEntity<Image> getImage(@RequestBody Long id) {
        Image i = dao.getImage(id);

        if (i != null) {
            return ResponseEntity.ok(i);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value="/api/image/setImage")
    public void setImage(@RequestBody Image i) {
        dao.modifyImage(i);
    }

    @PostMapping(value="/api/image/addImageToCollection")
    public ResponseEntity<String> addImageToCollection(@RequestBody Long Id_collection, Long Id_image) {
        Image i = dao.getImage(Id_image);
        Collection c = collection_DAO.getCollection(Id_collection);
        List<Image> list_image = c.getList_images();
        int length = list_image.size();
        list_image.add(i);
        c.setList_images(list_image);
        collection_DAO.modifyCollection(c);
        if (length == list_image.size()-1) {
            return ResponseEntity.ok("Image added");
        } else {
            return ResponseEntity.status(500).body("An error occured");
        }
    }

    @PostMapping(value="/api/image/removeImageFromCollection")
    public ResponseEntity<String> removeImageFromCollection(@RequestBody Long Id_collection, Long Id_image) {
        Image i = dao.getImage(Id_image);
        Collection c = collection_DAO.getCollection(Id_collection);
        List<Image> list_image = c.getList_images();
        int length = list_image.size();
        list_image.remove(i);
        c.setList_images(list_image);
        collection_DAO.modifyCollection(c);
        if (length == list_image.size()+1) {
            return ResponseEntity.ok("Image removed");
        } else {
            return ResponseEntity.status(500).body("An error occured");
        }
    }

    @PostMapping(value="/api/image/generate")
    public ResponseEntity<List<Image>> generateImages(@RequestBody Long userID, String instruction, String selectedButtons, String buttonLabelsByTab, int width, int height, int seed, int generationSteps, float guidanceScale) {


        JsonObject requestGson = new JsonObject();

        requestGson.addProperty("key", "");
        requestGson.addProperty("model_id", "base-model");
        requestGson.addProperty("prompt", instruction);
        requestGson.addProperty("negative_prompt", "");
        requestGson.addProperty("width", width);
        requestGson.addProperty("height", height);
        requestGson.addProperty("samples", 4);
        requestGson.addProperty("num_inference_steps", generationSteps);
        requestGson.addProperty("safety_checker", "no");
        requestGson.addProperty("enhance_prompt", "yes");
        requestGson.addProperty("seed", seed);
        requestGson.addProperty("guidance_scale", guidanceScale);
        requestGson.addProperty("multi_lingual", "no");
        requestGson.addProperty("panorama", "no");
        requestGson.addProperty("self_attention", "no");
        requestGson.addProperty("upscale", "no");
        requestGson.addProperty("embeddings_model", "embeddings_model_id");
        requestGson.addProperty("lora_model", "lora_model_id");
        requestGson.addProperty("tomesd", "yes");
        requestGson.addProperty("clip_skip", "2");
        requestGson.addProperty("use_karras_sigmas", "yes");
        requestGson.add("vae", null);
        requestGson.add("lora_strength", null);
        requestGson.addProperty("scheduler", "UniPCMultistepScheduler");
        requestGson.add("webhook", null);
        requestGson.add("track_id", null);

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        okhttp3.RequestBody body = okhttp3.RequestBody.create(requestGson.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url("https://stablediffusionapi.com/api/v4/dreambooth")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        try {
            Response response = client.newCall(request).execute();
            ObjectMapper mapper = new ObjectMapper();
            JsonObject responseJson = mapper.readValue(Objects.requireNonNull(response.body()).string(), JsonObject.class);

            List<Image> images = new ArrayList<>();

            JsonArray respUrls = responseJson.getAsJsonArray("output");

            for (JsonElement respUrl : respUrls) {
                Image newImage = new Image();
                newImage.setCreator(userDAO.getUser(userID));
                newImage.setPrompt(instruction);
                newImage.setNegative_prompt("");
                newImage.setModel("");
                newImage.setSeed(seed);
                newImage.setCfg_scale(newImage.getCfg_scale());
                newImage.setUrl_image(respUrl.toString());
                newImage.setNote(null);
                newImage.setHeight(height);
                newImage.setWidth(width);
                newImage.setVisible(true);

                images.add(newImage);
            }

            return ResponseEntity.ok(images);

        } catch(Exception e) {
            return ResponseEntity.notFound().build();
        }

    }
}
