package net.redsierra.Spacio.util;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

public class AddRoleToMember {
    public void add(Guild guild, String roleName, StringSelectInteractionEvent event) {
        assert event.getMember() != null;
        guild.addRoleToMember(event.getMember(), guild.getRolesByName(roleName, true).get(0)).queue();
        event.reply(  "You have given the role `" + roleName + "`").setEphemeral(true).queue();
    }

}
