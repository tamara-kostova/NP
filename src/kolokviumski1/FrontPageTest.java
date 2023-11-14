package kolokviumski1;

import java.util.*;
import java.util.stream.Collectors;

public class FrontPageTest {
    public static void main(String[] args) {
        // Reading
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        String[] parts = line.split(" ");
        Category[] categories = new Category[parts.length];
        for (int i = 0; i < categories.length; ++i) {
            categories[i] = new Category(parts[i]);
        }
        int n = scanner.nextInt();
        scanner.nextLine();
        FrontPage frontPage = new FrontPage(categories);
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < n; ++i) {
            String title = scanner.nextLine();
            cal = Calendar.getInstance();
            int min = scanner.nextInt();
            cal.add(Calendar.MINUTE, -min);
            Date date = cal.getTime();
            scanner.nextLine();
            String text = scanner.nextLine();
            int categoryIndex = scanner.nextInt();
            scanner.nextLine();
            TextNewsItem tni = new TextNewsItem(title, date, categories[categoryIndex], text);
            frontPage.addNewsItem(tni);
        }

        n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String title = scanner.nextLine();
            int min = scanner.nextInt();
            cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, -min);
            scanner.nextLine();
            Date date = cal.getTime();
            String url = scanner.nextLine();
            int views = scanner.nextInt();
            scanner.nextLine();
            int categoryIndex = scanner.nextInt();
            scanner.nextLine();
            MediaNewsItem mni = new MediaNewsItem(title, date, categories[categoryIndex], url, views);
            frontPage.addNewsItem(mni);
        }
        // Execution
        String category = scanner.nextLine();
        System.out.println(frontPage);
        for(Category c : categories) {
            System.out.println(frontPage.listByCategory(c).size());
        }
        try {
            System.out.println(frontPage.listByCategoryName(category).size());
        } catch(CategoryNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}


// Vasiot kod ovde
class FrontPage{
    List<NewsItem> newsItems;
    Category [] categories;

    public FrontPage(Category[] categories) {
        this.categories = categories;
        newsItems = new ArrayList<>();
    }
    public void addNewsItem(NewsItem newsItem){
        newsItems.add(newsItem);
    }
    public List<NewsItem> listByCategory(Category category){
        return newsItems.stream().filter(n->n.getCategory().getName().equals(category.getName())).collect(Collectors.toList());
    }
    public List<NewsItem> listByCategoryName(String category) throws CategoryNotFoundException{
        if (!Arrays.stream(categories).anyMatch(c->c.getName().equals(category)))
            throw new CategoryNotFoundException(category);
        return newsItems.stream().filter(n->n.getCategory().getName().equals(category)).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (NewsItem newsItem : newsItems)
            sb.append(newsItem.getTeaser());
        return sb.toString();
    }
}
class TextNewsItem extends NewsItem{
    String text;

    public TextNewsItem(String title, Date dateOfPublish, Category category, String text) {
        super(title, dateOfPublish, category);
        this.text = text;
    }
    @Override
    public String getTeaser() {
        StringBuilder sb = new StringBuilder();
        sb.append(getTitle()).append("\n");
        sb.append(minutes()).append("\n");
        for (int i=0; i<Math.min(80,text.length()); i++)
            sb.append(text.toCharArray()[i]);
        sb.append("\n");
        return sb.toString();
    }
}
class MediaNewsItem extends NewsItem{
    String url;
    int views;

    public MediaNewsItem(String title, Date dateOfPublish, Category category, String url, int views) {
        super(title, dateOfPublish, category);
        this.url = url;
        this.views = views;
    }
    @Override
    public String getTeaser() {
        StringBuilder sb = new StringBuilder();
        sb.append(getTitle()).append("\n");
        sb.append(minutes()).append("\n");
        sb.append(url).append("\n");
        sb.append(views).append("\n");
        return sb.toString();
    }
}
abstract class NewsItem {
    private String title;
    private Date dateOfPublish;
    private Category category;

    public NewsItem(String title, Date dateOfPublish, Category category) {
        this.title = title;
        this.dateOfPublish = dateOfPublish;
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public int minutes() {
        Date now = new Date();
        long miliseconds = now.getTime() - dateOfPublish.getTime();
        return (int) (miliseconds / 60000);
    }

    abstract String getTeaser();
}
class Category{
    String name;

    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
class CategoryNotFoundException extends Exception{
    public CategoryNotFoundException(String category) {
        super(String.format("Category %s was not found",category));
    }
}
