package net.redsierra.Spacio.events.interactions.menus;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

public class RolesStringMenu extends ListenerAdapter {
    private static final String ROLES_COMPONENT_ID = "roles";
    private static final String REGION_SELECT_COMPONENT_ID = "region-select";
    private static final String GENDER_SELECT_COMPONENT_ID = "gender-select";
    private static final String CATEGORIES_SELECT_COMPONENT_ID = "categories-select";
    private static final String PRONOUNS_SELECT_COMPONENT_ID = "pronouns-select";
    private static final Emoji EMOJI_EARTH = Emoji.fromUnicode("\ud83c\udf0d");
    private static final Emoji EMOJI_MALE = Emoji.fromUnicode("\u2642\ufe0f");
    private static final Emoji EMOJI_FEMALE = Emoji.fromUnicode("\u2640\ufe0f");
    private static final Emoji EMOJI_ART = Emoji.fromUnicode("\ud83c\udfa8");
    private static final Emoji EMOJI_MUSIC = Emoji.fromUnicode("\ud83c\udfb5");
    private static final Emoji EMOJI_GAMING = Emoji.fromUnicode("\uD83C\uDFAE");
    private static final Emoji EMOJI_PROGRAMMING = Emoji.fromUnicode("\ud83d\ude80");

    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getComponentId().equals(ROLES_COMPONENT_ID)) {
            String val = event.getValues().get(0);
            switch (val) {
                case "region":
                    event.reply("Please select your region.")
                            .addActionRow(
                                    StringSelectMenu.create(REGION_SELECT_COMPONENT_ID)
                                            .addOptions(SelectOption.of("North America", "na")
                                                    .withEmoji(EMOJI_EARTH)
                                                    .withDescription("North America"))
                                            .addOptions(SelectOption.of("South America", "sa")
                                                    .withEmoji(EMOJI_EARTH)
                                                    .withDescription("South America"))
                                            .addOptions(SelectOption.of("Europe", "eu")
                                                    .withEmoji(EMOJI_EARTH)
                                                    .withDescription("Europe"))
                                            .addOptions(SelectOption.of("Asia", "asia")
                                                    .withEmoji(EMOJI_EARTH)
                                                    .withDescription("Asia"))
                                            .addOptions(SelectOption.of("Africa", "africa")
                                                    .withEmoji(EMOJI_EARTH)
                                                    .withDescription("Africa"))
                                            .addOptions(SelectOption.of("Oceania", "oceania")
                                                    .withEmoji(EMOJI_EARTH)
                                                    .withDescription("Oceania"))
                                            .addOptions(SelectOption.of("Antarctica", "antarctica")
                                                    .withEmoji(EMOJI_EARTH)
                                                    .withDescription("Antarctica"))
                                            .build()
                            ).queue();
                break;
                case "gender":
                    event.reply("Please select the gender you identify with.")
                            .addActionRow(
                                    StringSelectMenu.create(GENDER_SELECT_COMPONENT_ID)
                                            .addOptions(SelectOption.of("Male", "male")
                                                    .withEmoji(EMOJI_MALE))
                                            .addOptions(SelectOption.of("Female", "female")
                                                    .withEmoji(EMOJI_FEMALE))
                                            .addOptions(SelectOption.of("Non-Binary", "non-binary"))
                                            .addOptions(SelectOption.of("Genderqueer", "genderqueer"))

                                    .build()
                            ).queue();
                break;
                case "categories":
                    event.reply("Please select the categories you are interested in.")
                            .addActionRow(
                                    StringSelectMenu.create(CATEGORIES_SELECT_COMPONENT_ID)
                                            .addOptions(SelectOption.of("Art", "art")
                                                    .withEmoji(EMOJI_ART))
                                            .addOptions(SelectOption.of("Music", "music")
                                                    .withEmoji(EMOJI_MUSIC))
                                            .addOptions(SelectOption.of("Gaming", "gaming")
                                                    .withEmoji(EMOJI_GAMING))
                                            .addOptions(SelectOption.of("Programming", "programming")
                                                    .withEmoji(EMOJI_PROGRAMMING))
                                    .build()
                            ).queue();
                break;
                case "pronouns":
                    event.reply("Please select the pronouns you use.")
                            .addActionRow(
                                    StringSelectMenu.create(PRONOUNS_SELECT_COMPONENT_ID)
                                            .addOptions(SelectOption.of("He/Him", "he-him"))
                                            .addOptions(SelectOption.of("She/Her", "she-her"))
                                            .addOptions(SelectOption.of("They/Them", "they-them"))
                                            .addOptions(SelectOption.of("Ask", "ask"))
                                    .build()
                            ).queue();
            }
        }
    }
}
