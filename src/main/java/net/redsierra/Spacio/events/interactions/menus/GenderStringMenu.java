package net.redsierra.Spacio.events.interactions.menus;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.redsierra.Spacio.util.AddRoleToMember;

public class GenderStringMenu extends ListenerAdapter {
    private static final String GENDER_SELECT_COMPONENT_ID = "gender-select";
    private static final String ROLE_FEMALE = "Female";
    private static final String ROLE_MALE = "Male";
    private static final String ROLE_NON_BINARY = "Non-Binary";
    private static final String ROLE_GENDERQUEER = "Genderqueer";

    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getComponentId().equals(GENDER_SELECT_COMPONENT_ID)) {
            String val = event.getValues().get(0);
            assert event.getMember() != null;
            assert event.getGuild() != null;
            Guild guild = event.getGuild();

            switch (val) {
                case "male":
                    new AddRoleToMember().add(guild, ROLE_MALE, event);
                break;
                case "female":
                    new AddRoleToMember().add(guild, ROLE_FEMALE, event);
                break;
                case "non-binary":
                    new AddRoleToMember().add(guild, ROLE_NON_BINARY, event);
                break;
                case "genderqueer":
                    new AddRoleToMember().add(guild, ROLE_GENDERQUEER, event);
                break;
            }
        }
    }
}
