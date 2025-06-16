
# 🧬 NeuroFuzz

**NeuroFuzz** is an intelligent API fuzzer written in Java. It sends custom payloads to HTTP endpoints, records responses, and helps identify unexpected behavior, server errors, and potential security vulnerabilities.

---

## 🚀 Features (Phase 1 – MVP)

- ✅ Simple and powerful CLI using Picocli
- ✅ Fuzzing with payloads from `.jsonl` files
- ✅ Optional payload mutation support (`--mutations`)
- ✅ Verbose mode (`-v`)
- ✅ Save responses to `.jsonl` file (`-s`)
- ✅ Filter to show only error responses (`-e`)
- ✅ Request timeout support (`-t`)
- ✅ Final report with response statistics (`NeuroFuzz Report`)

---

## 🛠️ Requirements

- Java 21+
- Maven 3.9.5+

---

## 📦 Build

```bash
mvn clean package
```

The executable jar will be generated at:

```
target/NeuroFuzz-0.1.0.jar
```

---

## ⚙️ Usage

```bash
java -jar NeuroFuzz-0.1.0.jar   -u https://example.com/api   -p payloads.jsonl   -ve   -t 3
```

### 🔹 CLI Parameters

| Flag               | Description                                       |
|--------------------|---------------------------------------------------|
| `-u, --url`        | Target URL to fuzz                                |
| `-p, --payloads`   | `.jsonl` file with payloads                       |
| `-m, --mutations`  | Enable payload mutations (optional)              |
| `-s, --save`       | Save results to a `.jsonl` file                  |
| `-e, --onlyerrors` | Display only errors (4xx, 5xx, timeouts)         |
| `-v, --verbose`    | Print each response to the console               |
| `-t, --timeout`    | Max request time in seconds                      |

---

## 📄 Sample `.jsonl` File

```json
{"input": "admin"}
{"input": "' OR 1=1 --"}
{"input": "<script>alert(1)</script>"}
```

---

## 📊 Sample Output Report

```
--- NeuroFuzz Report ---
Total payloads processed: 50
2xx: 42 | 4xx: 5 | 5xx: 2 | Timeout: 1
Saved to file: fuzz_20250612_1612.jsonl
```

---

## 🧩 Future Plans

- [ ] Custom HTTP headers (`--header`)
- [ ] Custom HTTP methods (`--method` or read from `.jsonl`)
- [ ] Learn from real traffic (Phase 2 or 3 – TBD)
- [ ] CI/CD integration for automated testing
- [ ] swagger/openapi.json support for API documentation
- [ ] IA-powered payload generation (Phase 3)

---

## 👨‍💻 Author

**Luis Hernández**  
Backend Java Developer | Spring Boot | API Security

---

## ⚠️ License

This project is licensed under the MIT License.
