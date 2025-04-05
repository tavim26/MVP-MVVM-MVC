package ViewModel.Commands;

public class AddArtistCommand implements ICommands {
    private final Runnable action;

    public AddArtistCommand(Runnable action) {
        this.action = action;
    }

    @Override
    public void execute() {
        if (action != null) {
            action.run();
        }
    }
}
