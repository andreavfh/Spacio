package net.redsierra.Spacio.events.interactions.slash.commands.general;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.redsierra.Spacio.Spacio;
import net.redsierra.Spacio.events.SlashCommandInteraction;
import net.redsierra.Spacio.events.interactions.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class Roles extends Command {

    public Logger logger = LoggerFactory.getLogger(Spacio.class);

    public Roles() {
        super.setName("roles");
        super.setCategory("info");
        super.setDescription("Get your info roles.");
    }
    @Override
    public void execute(SlashCommandInteraction commandEvent) {
        SlashCommandInteractionEvent event = commandEvent.event();
        assert event != null;
        Guild guild = event.getGuild();

        String[] requiredRoles = {"Art", "Music", "Gaming", "Programming", "Male", "Female", "Non-Binary", "Genderqueer", "North America", "South America", "Europe", "Asia", "Africa", "Oceania", "Antarctica", "He/Him", "She/Her", "They/Them", "Ask"};

        for (String requiredRole : requiredRoles) {
            assert guild != null;

            if (guild.getRolesByName(requiredRole, true).isEmpty()) {
                try {
                    guild.createRole().setName(requiredRole).queue();
                    logger.info("Created role " + requiredRole + " in guild " + guild.getName());
                } catch (Exception e) {
                    logger.error("Error creating role " + requiredRole + " in guild " + guild.getName());
                }
            }
        }

        event.reply("Please select an option below to get your info roles")
                .addActionRow(
                        StringSelectMenu.create("roles")
                                .addOptions(SelectOption.of("Region", "region")
                                        .withDescription("The region you live in")
                                        .withEmoji(Emoji.fromUnicode("\ud83c\udf0d")))
                                .addOptions(SelectOption.of("Gender", "gender")
                                        .withDescription("The gender you identify with")
                                        .withEmoji(Emoji.fromUnicode("\ud83d\udc64")))
                                .addOptions(SelectOption.of("Categories", "categories")
                                        .withDescription("The categories you are interested in")
                                        .withEmoji(Emoji.fromUnicode("\ud83d\ude80")))
                                .addOptions(SelectOption.of("Pronouns", "pronouns")
                                        .withDescription("The pronouns you use")
                                        .withEmoji(Emoji.fromUnicode("\u2615")))
                                .build()
                ).queue();
    }
}
