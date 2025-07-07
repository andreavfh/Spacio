package net.redsierra.Spacio.events.interactions.menus;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.redsierra.Spacio.util.AddRoleToMember;

public class CategoryStringMenu extends ListenerAdapter {
    private static final String CATEGORIES_SELECT_COMPONENT_ID = "categories-select";
    private static final String ROLE_ART = "Art";
    private static final String ROLE_MUSIC = "Music";
    private static final String ROLE_GAMING = "Gaming";
    private static final String ROLE_PROGRAMMING = "Programming";

    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getComponentId().equals(CATEGORIES_SELECT_COMPONENT_ID)) {
            String val = event.getValues().get(0);
            assert event.getMember() != null;
            assert event.getGuild() != null;
            Guild guild = event.getGuild();

            switch (val) {
                case "art":
                    new AddRoleToMember().add(guild, ROLE_ART, event);
                    break;
                case "music":
                    new AddRoleToMember().add(guild, ROLE_MUSIC, event);
                    break;
                case "gaming":
                    new AddRoleToMember().add(guild, ROLE_GAMING, event);
                    break;
                case "programming":
                    new AddRoleToMember().add(guild, ROLE_PROGRAMMING, event);
                    break;
            }
        }
    }
}
