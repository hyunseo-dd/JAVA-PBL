public class Task {
    public String title;
    public boolean done;

    public Task(String title) {
        this.title = title;
        this.done = false;
    }

    @Override
    public String toString() {
        if (done)
            return "<html><strike>" + title + "</strike></html>";
        else
            return title;
    }
}
