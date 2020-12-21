package me.rigamortis.seppuku.api.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.rigamortis.seppuku.api.util.FileUtil;
import org.apache.commons.io.FileUtils;
import scala.tools.nsc.Global;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author noil
 * ^ This retard can't open and close a file
 */
public abstract class Configurable {

    private File file;

    private JsonObject jsonObject;

    public Configurable(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void onLoad() {
        this.jsonObject = this.convertJsonObjectFromFile();
    }

    public void onSave() {

    }

    protected void saveJsonObjectToFile(JsonObject object) {
        File newFile = FileUtil.recreateFile(this.getFile());
        FileUtil.saveJsonFile(newFile, object);
    }

    protected JsonObject convertJsonObjectFromFile() {
        if (!this.getFile().exists()) {
            System.err.println(this.getFile() + " does not exist?");
            return new JsonObject();
        }

        JsonElement element = null;

        try {
            element = new JsonParser().parse(FileUtils.readFileToString(this.getFile(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("This error occurred while reading Gladiator config!");
        }

        if (element == null || !element.isJsonObject()) {
            System.err.printf("This error occurred while reading Gladiator config %s!%n", this.getFile());

            return new JsonObject();

        }

        return element.getAsJsonObject();
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }
}