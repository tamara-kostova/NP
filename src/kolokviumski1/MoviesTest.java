package kolokviumski1;

import java.util.*;

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
    private List<Movie> movies;

    public MoviesList() {
        movies = new ArrayList<>();
    }

    public void addMovie(String title, int[] ratings){
        movies.add(new Movie(title,ratings));
    }
    public List<Movie> top10ByAvgRating(){
        Collections.sort(movies,new AverageComparator().reversed());
        if (movies.size()>=10)
            return movies.subList(0,10);
        return movies;
    }
    public List<Movie> top10ByRatingCoef(){
        int max = 0;
        for (Movie movie : movies)
            max = Math.max(movie.numberOfRatings(),max);
        Collections.sort(movies,new CoefComparator(max).reversed());
        if (movies.size()>=10)
            return movies.subList(0,10);
        return movies;
    }

}
class Movie {
    private String Name;
    private int [] ratings;

    public Movie(String name, int[] ratings) {
        Name = name;
        this.ratings = new int [ratings.length];
        for (int i=0;i<ratings.length;i++) {
            this.ratings[i]=ratings[i];
        }
    }

    public String getName() {
        return Name;
    }

    public int numberOfRatings(){
        return ratings.length;
    }
    public double averageRating(){
        return (Arrays.stream(ratings).sum()*1.0)/ratings.length;
    }
    @Override
    public String toString() {
        return String.format("%s (%.2f) of %d ratings",Name,averageRating(),ratings.length);
    }
}
class CoefComparator implements Comparator<Movie>{
    private int maxNumberRatings;

    public CoefComparator(int maxNumberRatings) {
        this.maxNumberRatings = maxNumberRatings;
    }

    @Override
    public int compare(Movie o1, Movie o2) {
        int comp = Double.compare(o1.averageRating()*o1.numberOfRatings()/maxNumberRatings,o2.averageRating()*o2.numberOfRatings()/maxNumberRatings);
        if (comp==0)
            return o2.getName().compareTo(o1.getName());
        else return comp;
    }

}
class AverageComparator implements Comparator<Movie>{
    @Override
    public int compare(Movie o1, Movie o2) {
        int comp = Double.compare(o1.averageRating(),o2.averageRating());
        if (comp==0)
            return o2.getName().compareTo(o1.getName());
        return comp;
    }
}