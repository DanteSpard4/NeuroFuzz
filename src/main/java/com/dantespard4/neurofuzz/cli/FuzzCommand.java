package com.dantespard4.neurofuzz.cli;


import com.dantespard4.neurofuzz.core.Fuzzer;
import picocli.CommandLine.*;
import picocli.CommandLine.Model.CommandSpec;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
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

    @Option(names = {"-m","--mutations"}, description = "Tipos de mutaciones (separados por coma: replace-char,delete-key,insert-junk,repeat-key,empty-value)")
    private String mutationStrategies;

    @Option(names = {"-e", "--only-errors"}, description = "Mostrar solo respuestas con errores (4xx y 5xx)")
    private boolean onlyErrors;


    @Spec
    private CommandSpec spec;

    @Override
    public Integer call() {

        File resolvedFile = saveFile;
        Fuzzer fuzzer = new Fuzzer();

        fuzzer.setOnlyErrors(onlyErrors);

        var value = spec.optionsMap().get("--save").getValue();
        if (value != null && value.toString().isEmpty()) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            resolvedFile = new File("fuzz-results-" + timestamp + ".jsonl");
            System.out.println("[i] Resultados se guardarán en: " + resolvedFile.getName());
        }
        System.out.println("[*] Ejecutando fuzzer contra: " + url);

        if (mutationStrategies != null && !mutationStrategies.isEmpty()) {
            Set<String> strategies = new HashSet<>(Arrays.stream(mutationStrategies.split(","))
                    .map(String::trim)
                    .toList());
            fuzzer.setMutationStrategies(strategies);
            System.out.println("[*] Estrategias de mutación: " + strategies);
        } else {
            System.out.println("[*] No se especificaron estrategias de mutación, usando las predeterminadas.");
            fuzzer.setMutationStrategies(Set.of("replace-char"));
        }

        fuzzer.fuzzMultiple(url, payloadsFile, verbose,resolvedFile);
        return 0;
    }
}
