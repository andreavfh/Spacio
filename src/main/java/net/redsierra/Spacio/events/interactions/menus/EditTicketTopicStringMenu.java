package net.redsierra.Spacio.events.interactions.menus;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.redsierra.Spacio.util.EditTicketMessages;

public class EditTicketTopicStringMenu extends ListenerAdapter {

    private static final String EDIT_TICKET_TYPE_COMPONENT_ID = "edit-ticket-type";
    private static final String TOPIC_BUG_REPORT = "Bug Report";
    private static final String TOPIC_SUGGESTION = "Suggestion";
    private static final String TOPIC_DOUBTS = "Doubts";
    private static final String TOPIC_OTHER = "Other";

    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getComponentId().equals(EDIT_TICKET_TYPE_COMPONENT_ID)) {
            assert event.getGuild() != null;
            TextChannel channel = event.getGuild().getTextChannelById(event.getChannel().getId());
            assert channel != null;
            String val = event.getValues().get(0);

            switch (val) {
                case "bug-report":

                    String s = "Please describe your bug report in the following format:\n\n**Bug Report**\nWhat is the bug?\nHow can we reproduce it?\n\nTicket ID is: `" + event.getInteraction().getId() + "`";
                    new EditTicketMessages().createEmbed(s, event);
                    new EditTicketMessages().setTopicAndSendMessages(channel, TOPIC_BUG_REPORT, "Bug Report", s, event);
                    break;
                case "suggestion":

                    String s1 = "Please describe your suggestion in the following format:\n\n**Suggestion**\nWhat is your suggestion?\n\nTicket ID is: `" + event.getInteraction().getId() + "`";
                    new EditTicketMessages().createEmbed(s1, event);
                    new EditTicketMessages().setTopicAndSendMessages(channel, TOPIC_SUGGESTION, "Suggestion", s1, event);
                    break;
                case "doubts":

                    String s2 = "Please describe your doubts in the following format:\n\n**Doubts**\nWhat are your doubts?\n\nTicket ID is: `" + event.getInteraction().getId() + "`";
                    new EditTicketMessages().createEmbed(s2, event);
                    new EditTicketMessages().setTopicAndSendMessages(channel, TOPIC_DOUBTS, "Doubts", s2, event);
                    break;
                case "other":

                    String s3 = "Please describe your topic in the following format:\n\n**Other**\nWhat is your topic?\n\nTicket ID is: `" + event.getInteraction().getId() + "`";
                    new EditTicketMessages().createEmbed(s3, event);
                    new EditTicketMessages().setTopicAndSendMessages(channel, TOPIC_OTHER, "Other", s3, event);
                    break;
            }
        }
    }



}
