package me.rigamortis.seppuku.api.util;

import com.google.common.base.Utf8;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import me.rigamortis.seppuku.Seppuku;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author noil
 * @since 12/1/19 @ 4:16 PM
 */
public class FileUtil {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    /**
     * Tries to create a file reader
     */
    public static FileReader createReader(File file) {
        if (file.exists()) {
            try {
                return new FileReader(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Tries to close a file reader
     */
    public static void closeReader(FileReader reader) {
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a directory in the client's folder
     */
    public static File createDirectory(String dir) {
        File folder = new File(Seppuku.INSTANCE.getConfigManager().getConfigDir(), dir);
        if (!folder.exists())
            folder.mkdir();
        return folder;
    }

    /**
     * Creates a json file in a directory
     */
    public static File createJsonFile(File dir, String name) {
        File file = new File(dir, name + ".json");
        return file;
    }

    /**
     * Removes and existing file or creates a new file
     */
    public static File recreateFile(File file) {
        if (file.exists()) {
            file.delete();
        } else {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * Saves a json object to a file
     */
    public static void saveJsonFile(File file, JsonObject jsonObject) {
        String s = gson.toJson(jsonObject);
        try {
            FileUtils.writeStringToFile(file, s, StandardCharsets.UTF_8, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        try {
//            if (file.exists()) {
//                file.delete();
//            }
//            file.createNewFile();
//            FileWriter writer = new FileWriter(file);
//            Throwable throwable = null;
//            try {
//                writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(jsonObject));
//            } catch (Throwable var6_9) {
//                throwable = var6_9;
//                throw var6_9;
//            } finally {
//                if (throwable != null) {
//                    try {
//                        writer.close();
//                    } catch (Throwable var6_8) {
//                        throwable.addSuppressed(var6_8);
//                    }
//                } else {
//                    writer.close();
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            file.delete();
//        }
    }

    public static List<String> read(final File inputFile) {
        final List<String> fileContentList = new ArrayList<>();
        FileReader fileReader = null;
        BufferedReader bufferedFileReader = null;

        try {
            fileReader = new FileReader(inputFile);
            bufferedFileReader = new BufferedReader(fileReader);

            String currentReadLine;

            while ((currentReadLine = bufferedFileReader.readLine()) != null) {
                fileContentList.add(currentReadLine);
            }
        } catch (FileNotFoundException e) {
            System.err.println("FileNotFoundException thrown, make sure the file exists.");
        } catch (IOException e) {
            System.err.println("IOException thrown, can't read the file's content.");
        } finally {
            try {
                if (bufferedFileReader != null) {
                    bufferedFileReader.close();
                }
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (IOException e) {
            }
        }

        return fileContentList;
    }

    public static String write(final File outputFile, final List<String> writeContent, boolean override) {
        BufferedWriter bufferedFileWriter = null;
        FileWriter fileWriter = null;
        String message = "";

        try {
            fileWriter = new FileWriter(outputFile, !override);
            bufferedFileWriter = new BufferedWriter(fileWriter);

            for (final String outputLine : writeContent) {
                bufferedFileWriter.write(outputLine);
                bufferedFileWriter.flush();
                bufferedFileWriter.newLine();
            }

            message = "Completed writing to the file.";
        } catch (IOException e) {
            message = "IOException thrown while attempting to write.";
        } finally {
            try {
                if (bufferedFileWriter != null) {
                    bufferedFileWriter.close();
                }
                if (fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException e) {
                message = "IOException thrown while attemping to close the writer.";
            }
        }

        return message;
    }

    public static boolean ensureExistance(File targetFile) {
        if (!targetFile.exists()) {
            try {
                targetFile.createNewFile();
            } catch (IOException e) {
                System.err.println("IOException thrown, can't create file.");
            }
        }

        return targetFile.exists();
    }
}
