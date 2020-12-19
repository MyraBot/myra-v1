package com.myra.dev.marian.utilities.APIs;

import com.myra.dev.marian.Bot;
import com.myra.dev.marian.utilities.Config;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.entities.User;
import okhttp3.Request;
import org.json.JSONObject;

import java.io.IOException;

public class DiscordBoats {
    private final static DiscordBoats DISCORD_BOATS = new DiscordBoats();

    public static DiscordBoats getInstance() {
        return DISCORD_BOATS;
    }

    public boolean hasVoted(User user) throws IOException {
        // Create request
        Request request = new Request.Builder()
                .url("https://discord.boats/api/bot/" + Config.myra + "/voted?id=" + user.getId())
                .build();
        final String response = Utilities.HTTP_CLIENT.newCall(request).execute().body().string(); // Get response
        if (new JSONObject(response).getBoolean("error")) return false; // User hasn't logged in in discord.boats yet
        return new JSONObject(response).getBoolean("voted"); // Return voted state
    }
}
