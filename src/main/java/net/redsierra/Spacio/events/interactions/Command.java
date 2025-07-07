package net.redsierra.Spacio.events.interactions;


import net.redsierra.Spacio.events.SlashCommandInteraction;

public abstract class Command {

    private String name;
    private String description;
    private String category;

    public String setName(String name) {
        return this.name = name;
    }
    public String setDescription(String description) {
        return this.description = description;
    }
    public String setCategory(String category) {
        return this.category = category;
    }

    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public String getCategory() {
        return category;
    }

    public abstract void execute(SlashCommandInteraction commandEvent);
}