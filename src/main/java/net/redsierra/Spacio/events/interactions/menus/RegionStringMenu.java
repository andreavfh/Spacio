package net.redsierra.Spacio.events.interactions.menus;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.redsierra.Spacio.util.AddRoleToMember;

public class RegionStringMenu extends ListenerAdapter {
    private static final String REGION_SELECT_COMPONENT_ID = "region-select";
    private static final String ROLE_NA = "North America";
    private static final String ROLE_SA = "South America";
    private static final String ROLE_EU = "Europe";
    private static final String ROLE_ASIA = "Asia";
    private static final String ROLE_AFRICA = "Africa";
    private static final String ROLE_OCEANIA = "Oceania";
    private static final String ROLE_ANTARCTICA = "Antarctica";

    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getComponentId().equals(REGION_SELECT_COMPONENT_ID)) {
            String val = event.getValues().get(0);
            assert event.getMember() != null;
            assert event.getGuild() != null;
            Guild guild = event.getGuild();

            switch (val) {
                case "na":
                    new AddRoleToMember().add(guild, ROLE_NA, event);
                break;
                case "sa":
                    new AddRoleToMember().add(guild, ROLE_SA, event);
                break;
                case "eu":
                    new AddRoleToMember().add(guild, ROLE_EU, event);
                break;
                case "asia":
                    new AddRoleToMember().add(guild, ROLE_ASIA, event);
                break;
                case "africa":
                    new AddRoleToMember().add(guild, ROLE_AFRICA, event);
                break;
                case "oceania":
                    new AddRoleToMember().add(guild, ROLE_OCEANIA, event);
                break;
                case "antarctica":
                    new AddRoleToMember().add(guild, ROLE_ANTARCTICA, event);
                break;
            }
        }
    }
}
