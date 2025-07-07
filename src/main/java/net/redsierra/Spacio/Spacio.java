package net.redsierra.Spacio;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.redsierra.Spacio.config.BotConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.EnumSet;
import java.util.List;

public class Spacio {

    public Logger logger = LoggerFactory.getLogger(Spacio.class);
    private static ShardManager shardManager;
    public static void main(String[] args) throws LoginException {

        BotConfig config = new BotConfig();
        String token = config.getBotToken();
        List<ListenerAdapter> listeners = ListenerLoader.loadListeners("net.redsierra.Spacio.events");

        shardManager = DefaultShardManagerBuilder.createDefault(token)
                .setShardsTotal(config.getShardCount())
                .enableIntents(EnumSet.of(
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.MESSAGE_CONTENT
                ))
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setActivity(Activity.listening("DROGA - Mora & C. Tangana"))
                .addEventListeners(listeners.toArray())
                .build();

    }

    public static ShardManager getShardManager() {
        return shardManager;
    }
}