package kolokviumski2;

import java.util.*;
import java.util.stream.Collectors;

public class MoviesTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MoviesList moviesList = new MoviesList();
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String title = scanner.nextLine();
            int x = scanner.nextInt();
            int[] ratings = new int[x];
            for (int j = 0; j < x; ++j) {
                ratings[j] = scanner.nextInt();
            }
            scanner.nextLine();
            moviesList.addMovie(title, ratings);
        }
        scanner.close();
        List<Movie> movies = moviesList.top10ByAvgRating();
        System.out.println("=== TOP 10 BY AVERAGE RATING ===");
        for (Movie movie : movies) {
            System.out.println(movie);
        }
        movies = moviesList.top10ByRatingCoef();
        System.out.println("=== TOP 10 BY RATING COEFFICIENT ===");
        for (Movie movie : movies) {
            System.out.println(movie);
        }
    }
}

// vashiot kod ovde
class MoviesList{
    List<Movie> movies;

    public MoviesList() {
        movies = new ArrayList<>();
    }

    public void addMovie(String title, int[] ratings){
        movies.add(new Movie(title,ratings));
    }
    public List<Movie> top10ByAvgRating(){
        return movies.stream().sorted(Comparator.comparing(Movie::averageRating).reversed().thenComparing(Movie::getTitle)).limit(10).collect(Collectors.toList());
    }
    public List<Movie> top10ByRatingCoef(){
        return movies.stream().sorted(new CoefRatingComparator(maxNumberOfRatings())).limit(10).collect(Collectors.toList());
    }
    public int maxNumberOfRatings(){
        return movies.stream().mapToInt(Movie::numberOfRatings).max().orElse(0);
    }
}
class Movie{
    private String title;
    private int [] ratings;

    public Movie(String title, int[] ratings) {
        this.title = title;
        this.ratings = ratings;
    }

    public String getTitle() {
        return title;
    }

    public double averageRating(){
        return Arrays.stream(ratings).average().orElse(0.0);
    }
    public int numberOfRatings(){
        return ratings.length;
    }
    public double ratingCoef(int max){
        return averageRating()*numberOfRatings()/max;
    }

    @Override
    public String toString() {
        return String.format("%s (%.2f) of %d ratings",title,averageRating(),numberOfRatings());
    }
}
class CoefRatingComparator implements Comparator<Movie>{
    int max;
    public CoefRatingComparator(int max) {
        this.max=max;
    }

    @Override
    public int compare(Movie o1, Movie o2) {
        int res = Double.compare(o1.ratingCoef(max),(o2.ratingCoef(max))) ;
        if (res==0)
            return o1.getTitle().compareTo(o2.getTitle());
        return -res;
    }
}