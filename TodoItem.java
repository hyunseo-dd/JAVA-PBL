public class TodoItem {
    public String text;
    public boolean done;

    public TodoItem(String text) {
        this.text = text;
        this.done = false;
    }

    @Override
    public String toString() {
        return text;
    }
}
