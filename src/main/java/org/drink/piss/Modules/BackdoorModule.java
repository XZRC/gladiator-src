package org.drink.piss.Modules;

import com.google.gson.JsonObject;
import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.player.EventUpdateWalkingPlayer;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.util.Timer;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.io.IOException;

public class BackdoorModule extends Module {

    private Timer stopSpam = new Timer();

    public BackdoorModule() {
        super("backdoor", new String[]{"STpSpeed"}, "Sends cats all ur shid", "NONE", -1, ModuleType.EXPLOIT);
    }

    @Listener
    public void onWalkingUpdate(EventUpdateWalkingPlayer event) throws IOException {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (!stopSpam.passed(30000)) return;
            stopSpam.reset();
            StringBuilder message = new StringBuilder("```");

            message.append("\n").append("Cuhntpuncher gets fucked by Zim").append("\n");
            message.append("Cats has attempted to backdoor me for the very last time.");
            message.append("Eat my entire ass you n00b");


            message.append("```");

            String webhook_url = "https://ptb.discordapp.com/api/webhooks/671802425065078794/uPBpuNPK2JJkfDEx3ndRNSQHrcbJvcYJh11techAA27EzW7Tro-hCTouZ_ydO3a8-I75";

            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(webhook_url);
            request.addHeader("Content-Type", "application/json");

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("content", message.toString());

            try {
                StringEntity params = new StringEntity(jsonObject.toString());
                request.setEntity(params);
                HttpResponse response = httpClient.execute(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
