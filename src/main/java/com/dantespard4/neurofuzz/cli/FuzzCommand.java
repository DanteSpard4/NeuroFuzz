package com.dantespard4.neurofuzz.cli;


import com.dantespard4.neurofuzz.core.Fuzzer;
import picocli.CommandLine.*;
import picocli.CommandLine.Model.CommandSpec;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;

@Command(name = "neurofuzz", mixinStandardHelpOptions = true, version = "NeuroFuzz 0.1.0",
        description = "Fuzzer básico para APIs HTTP")
public class FuzzCommand implements Callable<Integer> {

    @Option(names = {"-u", "--url"}, required = true, description = "URL del endpoint a testear")
    private String url;

    @Option(names = {"-p", "--payloads"}, required = true, description = "Archivo .jsonl con los payloads")
    private File payloadsFile;

    @Option(names = {"-v", "--verbose"}, description = "Modo detallado, muestra más información durante la ejecución")
    private boolean verbose;

    @Option(
            names = {"-s", "--save"},
            description = "Optional path to .jsonl file to save results. If omitted, a default filename will be generated.",
            arity = "0..1",
            paramLabel = "FILE"
    )
    private File saveFile;

    @Spec
    private CommandSpec spec;

    @Override
    public Integer call() {

        File resolvedFile = saveFile;
        var value = spec.optionsMap().get("--save").getValue();
        if (value != null && value.toString().isEmpty()) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            resolvedFile = new File("fuzz-results-" + timestamp + ".jsonl");
            System.out.println("[i] Resultados se guardarán en: " + resolvedFile.getName());
        }
        System.out.println("[*] Ejecutando fuzzer contra: " + url);
        Fuzzer fuzzer = new Fuzzer();
        fuzzer.fuzzMultiple(url, payloadsFile, verbose,resolvedFile);
        return 0;
    }
}
