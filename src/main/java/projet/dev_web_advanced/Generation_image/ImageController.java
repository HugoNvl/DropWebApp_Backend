package projet.dev_web_advanced.Generation_image;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ImageController {

    private ImageDAO dao;
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
    public ResponseEntity<Image> generateImage(@RequestBody Long userID, String instruction, String selectedButtons, String buttonLabelsByTab, int width, int height, String seed, int generationSteps, float guidanceScale) {
        
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, "{\n    \"key\": Qun1A18qioEOi9p6QmEqkEENwPwePQbXnBibjSG5ujyACdyaiEYn4BZ0Dzdr\"\",\n  \"model_id\": \"base-model\",\n  \"prompt\": \"" + instruction + "\",\n  \"negative_prompt\": \"\",\n  \"width\": \"" + width + "\",\n  \"height\": \"" + height + "\",\n  \"samples\": \"4\",\n  \"num_inference_steps\": \"" + generationSteps + "\",\n  \"safety_checker\": \"no\",\n  \"enhance_prompt\": \"yes\",\n  \"seed\": \"" + seed + "\",\n  \"guidance_scale\": \"" + guidanceScale + "\",\n  \"multi_lingual\": \"no\",\n  \"panorama\": \"no\",\n  \"self_attention\": \"no\",\n  \"upscale\": \"no\",\n  \"embeddings_model\": \"embeddings_model_id\",\n  \"lora_model\": \"lora_model_id\",\n  \"tomesd\": \"yes\",\n  \"use_karras_sigmas\": \"yes\",\n  \"vae\": null,\n  \"lora_strength\": null,\n  \"scheduler\": \"UniPCMultistepScheduler\",\n  \"webhook\": null,\n  \"track_id\": null\n}");
        Request request = new Request.Builder().url("https://stablediffusionapi.com/api/v4/dreambooth").method("POST", body).addHeader("Content-Type", "application/json").build();
        
        try {
            Response response = client.newCall(request).execute();
            String resp = response.body().string();
            
        }
        catch(Exception e) {
            return ResponseEntity.notFound().build();
        }
        
        /*String url = "";
        Image newImage = new Image();
        newImage.setCreator(user);
        newImage.setPrompt(instruction);
        //TODO: other fields

        dao.createImage(newImage);

        return ResponseEntity.ok(new Image());*/
    }

}
