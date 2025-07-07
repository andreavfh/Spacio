package net.redsierra.Spacio.util;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class ChannelValidator {

    /**
     * Valida si el ID dado representa un canal válido en el servidor,
     * y si el bot tiene permiso para hablar en él.
     *
     * @param guild        El servidor (Guild)
     * @param channelIdRaw El ID del canal (puede ser una mención como <#123>)
     * @return El TextChannel válido si todo está bien, o null si falla
     */
    public static TextChannel validateTextChannel(Guild guild, String channelIdRaw) {
        if (guild == null || channelIdRaw == null) return null;

        // Eliminar símbolos de mención si los hay
        String cleanedId = channelIdRaw.replaceAll("[<#>]", "");

        // Validar que sea un número largo (Snowflake)
        if (!cleanedId.matches("\\d{17,20}")) return null;

        // Obtener canal
        TextChannel channel = guild.getTextChannelById(cleanedId);

        // Validar existencia y permisos
        if (channel == null || !channel.canTalk()) return null;

        return channel;
    }
}
