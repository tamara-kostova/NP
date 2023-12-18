package kolokviumski2;

import java.util.*;


public class PostTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String postAuthor = sc.nextLine();
        String postContent = sc.nextLine();

        Post p = new Post(postAuthor, postContent);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split(";");
            String testCase = parts[0];

            if (testCase.equals("addComment")) {
                String author = parts[1];
                String id = parts[2];
                String content = parts[3];
                String replyToId = null;
                if (parts.length == 5) {
                    replyToId = parts[4];
                }
                p.addComment(author, id, content, replyToId);
            } else if (testCase.equals("likes")) { //likes;1;2;3;4;1;1;1;1;1 example
                for (int i = 1; i < parts.length; i++) {
                    p.likeComment(parts[i]);
                }
            } else {
                System.out.println(p);
            }

        }
    }
}
class Post{
    String postAuthor;
    String postContent;
    List<Comment> comments;
    Map<String, Comment> commentsById;
    public Post(String postAuthor, String postContent) {
        this.postAuthor = postAuthor;
        this.postContent = postContent;
        comments = new ArrayList<>();
        commentsById = new HashMap<>();
    }

    public void addComment(String author, String id, String content, String replyToId) {
        Comment newcomment = new Comment(id, author, content);
        commentsById.putIfAbsent(id,newcomment);
        if (replyToId==null) {
            comments.add(newcomment);
        }
        else {
            commentsById.get(replyToId).addReply(newcomment);
        }
    }

    public void likeComment(String commentId) {
        commentsById.get(commentId).addLike();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Post: %s\nWritten by: %s\nComments:\n",postContent,postAuthor));
        comments.stream().sorted(Comparator.comparing(Comment::totalLikes).reversed()).forEach(comment -> sb.append(comment.printComment("        ")));
        return sb.toString();
    }
}
class Comment{
    String id;
    String commentAuthor;
    String commentContent;
    int likes;
    List<Comment> replies;

    public Comment(String id, String commentAuthor, String commentContent) {
        this.id = id;
        this.commentAuthor = commentAuthor;
        this.commentContent = commentContent;
        likes=0;
        replies = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void addLike(){
        likes++;
    }

    public void addReply(Comment reply){
        replies.add(reply);
    }

    public int totalLikes(){
        int sum = likes;
        sum+=replies.stream().mapToInt(Comment::totalLikes).sum();
        return sum;
    }

    public String printComment(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%sComment: %s\n%sWritten by: %s\n%sLikes: %d\n",indent,commentContent,indent,commentAuthor,indent,likes));
        replies.stream().sorted(Comparator.comparing(Comment::totalLikes).reversed()).forEach(reply-> sb.append(reply.printComment(indent+"    ")));
        return sb.toString();
    }
}