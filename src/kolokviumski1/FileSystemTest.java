package kolokviumski1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class FileSystemTest {

    public static Folder readFolder (Scanner sc)  {

        Folder folder = new Folder(sc.nextLine());
        int totalFiles = Integer.parseInt(sc.nextLine());

        for (int i=0;i<totalFiles;i++) {
            String line = sc.nextLine();

            if (line.startsWith("0")) {
                String fileInfo = sc.nextLine();
                String [] parts = fileInfo.split("\\s+");
                try {
                    folder.addFile(new File(parts[0], Long.parseLong(parts[1])));
                } catch (FileNameExistsException e) {
                    System.out.println(e.getMessage());
                }
            }
            else {
                try {
                    folder.addFile(readFolder(sc));
                } catch (FileNameExistsException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        return folder;
    }

    public static void main(String[] args)  {

        //file reading from input

        Scanner sc = new Scanner (System.in);

        System.out.println("===READING FILES FROM INPUT===");
        FileSystem fileSystem = new FileSystem();
        try {
            fileSystem.addFile(readFolder(sc));
        } catch (FileNameExistsException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("===PRINTING FILE SYSTEM INFO===");
        System.out.println(fileSystem.toString());

        System.out.println("===PRINTING FILE SYSTEM INFO AFTER SORTING===");
        fileSystem.sortBySize();
        System.out.println(fileSystem.toString());

        System.out.println("===PRINTING THE SIZE OF THE LARGEST FILE IN THE FILE SYSTEM===");
        System.out.println(fileSystem.findLargestFile());




    }
}
interface IFile extends Comparable<IFile>{
    String getFileName();
    long getFileSize();
    String getFileInfo(String indent);
    void sortBySize();
    long findLargestFile ();
}
class File implements IFile{
    private String fileName;
    private long fileSize;

    public File(String fileName, long fileSize) {
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public long getFileSize() {
        return fileSize;
    }

    @Override
    public String getFileInfo(String indent) {
        return indent+String.format("File name: %10s File size:%10d\n",fileName,fileSize);
    }

    @Override
    public void sortBySize() {
        return;
    }

    @Override
    public long findLargestFile() {
        return fileSize;
    }

    @Override
    public int compareTo(IFile o) {
        if (fileSize>o.getFileSize())
            return 1;
        if (fileSize<o.getFileSize())
            return -1;
        return 0;
    }
}
class Folder implements IFile{
    private String folderName;
    private List<IFile> files;

    public Folder(String folderName) {
        this.folderName = folderName;
        files = new ArrayList<>();
    }

    public List<IFile> getFiles() {
        return files;
    }

    @Override
    public String getFileName() {
        return folderName;
    }

    @Override
    public long getFileSize() {
        return files.stream().mapToLong(f->f.getFileSize()).sum();
    }

    @Override
    public String getFileInfo(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append(String.format("Folder name: %10s Folder size:%10d\n",folderName,getFileSize()));
        for (IFile file : files){
            sb.append(indent).append(file.getFileInfo("\t"));
        }
        return sb.toString();
    }

    @Override
    public void sortBySize() {
        Collections.sort(files);
        files.forEach(IFile::sortBySize);
    }

    @Override
    public long findLargestFile() {
        return files.stream().mapToLong(f->f.findLargestFile()).max().orElse(0);
    }
    void addFile (IFile file) throws FileNameExistsException{
        if (files.stream().map(IFile::getFileName).anyMatch(p->p.equals(file.getFileName())))
            throw new FileNameExistsException(file.getFileName(),folderName);
        files.add(file);
    }
    @Override
    public int compareTo(IFile o) {
        if (getFileSize()>o.getFileSize())
            return 1;
        if (getFileSize()<o.getFileSize())
            return -1;
        return 0;
    }
}
class FileSystem{
    private Folder rootDirectory;

    public FileSystem() {
        rootDirectory = new Folder("root");
    }

    public void addFile (IFile file) throws FileNameExistsException{
        rootDirectory.addFile(file);
    }
    public long findLargestFile (){
        return rootDirectory.findLargestFile();
    }
    public void sortBySize(){
        rootDirectory.sortBySize();
    }

    @Override
    public String toString() {
        return rootDirectory.getFileInfo("");
    }
}
class FileNameExistsException extends Exception{
    public FileNameExistsException(String fileName, String folderName) {
        super(String.format("There is already a file named %s in the folder %s",fileName,folderName));
    }
}
