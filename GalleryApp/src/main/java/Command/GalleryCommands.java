package Command;

public class GalleryCommands implements ICommands
{
    private final Runnable action;

    public GalleryCommands(Runnable action)
    {
        this.action = action;
    }

    @Override
    public void execute()
    {
        if (action != null)
        {
            action.run();
        }
    }
}