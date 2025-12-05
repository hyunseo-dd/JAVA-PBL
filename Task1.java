public class Task {
    public String text;
    public boolean done;

    public Task(String text) {
        this.text = text;
        this.done = false;
    }

    @Override
    public String toString() {
        return text;
    }
}
