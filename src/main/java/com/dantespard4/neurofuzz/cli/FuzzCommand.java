package com.dantespard4.neurofuzz.cli;


import com.dantespard4.neurofuzz.core.Fuzzer;
import picocli.CommandLine.*;

import java.io.File;

@Command(name = "neurofuzz", mixinStandardHelpOptions = true, version = "NeuroFuzz 0.1.0",
        description = "Fuzzer básico para APIs HTTP")
public class FuzzCommand implements Runnable {

    @Option(names = {"-u", "--url"}, required = true, description = "URL del endpoint a testear")
    private String url;

    @Option(names = {"-p", "--payloads"}, required = true, description = "Archivo .jsonl con los payloads")
    private File payloadsFile;

    @Override
    public void run() {
        System.out.println("[*] Ejecutando fuzzer contra: " + url);
        Fuzzer fuzzer = new Fuzzer();
        fuzzer.fuzzMultiple(url, payloadsFile);
    }
}
