import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TodoManager {
    private List<TodoItem> todoList;

    public TodoManager() {
        this.todoList = new ArrayList<>();
    }
    public void addItem(TodoItem item) {
        this.todoList.add(item);
        System.out.printf("'%s' 할 일이 추가되었습니다. (ID: %s)\n", item.getName(), item.getId().toString().substring(0, 4));
    }
    public TodoItem findItemById(UUID itemId) {
        for (TodoItem item : todoList) {
            if (item.getId().equals(itemId)) {
                return item;
            }
        }
        System.out.printf("ID '%s' 에 해당하는 할 일을 찾을 수 없습니다.\n", itemId.toString().substring(0, 4));
        return null;
    }
    public boolean deleteItem(UUID itemId) {
        TodoItem itemToDelete = findItemById(itemId);
        if (itemToDelete != null) {
            this.todoList.remove(itemToDelete);
            System.out.printf("'%s' 할 일이 삭제되었습니다.\n", itemToDelete.getName());
            return true;
        }
        return false;
    }
    public List<TodoItem> getAllItems(boolean includeCompleted) {
        if (includeCompleted) {
            return new ArrayList<>(this.todoList);
        }
        return this.todoList.stream()
        .filter(item -> !item.isCompleted())
        .collect(Collectors.toList());
    }
    public void sortItems(String sortKey) {
        sortItems(sortKey, false);
    }
    public void sortItems(String sortKey, boolean reverse) {
        Comparator<TodoItem> comparator = null;
        if ("priority".equals(sortKey)) {
            comparator = Comparator
            .comparing(TodoItem::isCompleted)
            .thenComparingInt(TodoItem::getPriority);
            System.out.println("할 일 목록이 우선순위 기준으로 정렬되었습니다.");
        } else if ("dueDate".equals(sortKey)) {
            comparator = Comparator
            .comparing(TodoItem::isCompleted)
            .thenComparing(item -> item.getDueDate() == null ? LocalDate.MAX:item.getDueDate());
            System.out.println("할 일 목록이 마감 기한 기준으로 정렬되었습니다.");
        } else {
            System.out.printf("경고: 알 수 없는 정렬 기준 '%s' 입니다.\n", sortKey);
            return;
        }
        if (reverse) {
            todoList.sort(comparator.reversed());
        } else {
            todoList.sort(comparator);
        }
    }
    public List<TodoItem> searchItems(String keyword) {
        List<TodoItem> results = this.todoList.stream().filter(item -> item.getName().toLowerCase().contains(keyword.toLowerCase()))
        .collect(Collectors.toList());
        System.out.printf("'%s' 검색 결과:\n", keyword);
        if (results.isEmpty()) {
            System.out.println("검색 결과가 없습니다.");
        }
        return results;
    }
}