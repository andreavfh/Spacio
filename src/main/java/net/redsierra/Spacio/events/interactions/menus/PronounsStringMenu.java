package net.redsierra.Spacio.events.interactions.menus;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.redsierra.Spacio.util.AddRoleToMember;

public class PronounsStringMenu extends ListenerAdapter {
    private static final String PRONOUNS_SELECT_COMPONENT_ID = "pronouns-select";
    private static final String ROLE_HE_HIM = "He/Him";
    private static final String ROLE_SHE_HER = "She/Her";
    private static final String ROLE_THEY_THEM = "They/Them";
    private static final String ROLE_ASK = "ASK";

     public void onStringSelectInteraction(StringSelectInteractionEvent event) {
         if (event.getComponentId().equals(PRONOUNS_SELECT_COMPONENT_ID)) {
             String val = event.getValues().get(0);
             assert event.getMember() != null;
             assert event.getGuild() != null;
             Guild guild = event.getGuild();

             switch (val) {
                 case "he-him":
                     new AddRoleToMember().add(guild, ROLE_HE_HIM, event);
                     break;
                 case "she-her":
                     new AddRoleToMember().add(guild, ROLE_SHE_HER, event);
                     break;
                 case "they-them":
                     new AddRoleToMember().add(guild, ROLE_THEY_THEM, event);
                     break;
                 case "ask":
                     new AddRoleToMember().add(guild, ROLE_ASK, event);
                     break;
             }
         }
     }
}
