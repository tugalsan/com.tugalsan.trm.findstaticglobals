package com.tugalsan.trm.findstaticglobals;

import com.tugalsan.api.file.server.*;
import com.tugalsan.api.file.txt.server.*;
import com.tugalsan.api.log.server.*;
import com.tugalsan.api.string.client.*;
import java.nio.file.*;
import java.util.stream.*;

//WHEN RUNNING IN NETBEANS, ALL DEPENDENCIES SHOULD HAVE TARGET FOLDER!
public class Main {

    final private static TS_Log d = TS_Log.of(Main.class);

    public static void main(String... s) {
        var parentDirectory = Path.of("C:\\git");
        var pattern = "src\\main\\java\\com\\tugalsan\\";

        run(parentDirectory, pattern);
    }

    /*
    it finds static variable usage    
    example input params
        var parentDirectory = Path.of("C:\\git");
        var pattern = "src\\main\\java\\com\\tugalsan\\";
     */
    public static void run(Path parentDirectory, String pattern) {
        if (!TS_DirectoryUtils.isExistDirectory(parentDirectory)) {
            d.ce("reportStaticGlobals", "skip", "dir not exists", parentDirectory);
            return;
        }
        d.ce("reportStaticGlobals", "started...");
        var results = parentDirectory.resolve("results.log");
        TS_FileUtils.deleteFileIfExists(results);
        TS_DirectoryUtils.subFiles(parentDirectory, "*.java", false, true).forEach(javaFile -> {
            var lines = TS_FileTxtUtils.toList(javaFile);
            IntStream.range(0, lines.size())
                    .filter(idx -> lines.get(idx).contains("static"))
                    .filter(idx -> !lines.get(idx).contains("import"))
                    .filter(idx -> !lines.get(idx).contains("class"))
                    .filter(idx -> !lines.get(idx).contains("interface"))
                    .filter(idx -> !lines.get(idx).contains("("))
                    .forEach(idx -> {
                        var ref = javaFile.toString();
                        var idxRef = ref.indexOf(pattern);
                        ref = ref.substring(idxRef + pattern.length());
                        var debugLine = TGS_StringUtils.cmn().concat(ref, ", ", String.valueOf(idx), ", ", lines.get(idx), "\n");
                        TS_FileTxtUtils.toFile(debugLine, results, true);
                    });
        });
        d.ce("reportStaticGlobals", "results", results);
    }
}
