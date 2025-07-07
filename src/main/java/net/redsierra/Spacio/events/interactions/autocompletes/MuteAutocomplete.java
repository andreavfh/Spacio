package net.redsierra.Spacio.events.interactions.autocompletes;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MuteAutocomplete extends ListenerAdapter {
    private final String[] words = new String[]{"1m", "5m", "10m", "1h", "2h", "3h", "1d", "2d", "3d", "1w"};

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if (event.getName().equals("mute") && event.getFocusedOption().getName().equals("time")) {
            List<Command.Choice> options = Stream.of(words)
                    .filter(word -> word.startsWith(event.getFocusedOption().getValue()))
                    .map(word -> new Command.Choice(word, word))
                    .collect(Collectors.toList());
            event.replyChoices(options).queue();
        }
    }
}
