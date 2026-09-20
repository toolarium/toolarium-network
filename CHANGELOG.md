# toolarium-network

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [ 1.1.4 ] - 2026-09-20

## [ 1.1.3 ] - 2026-09-20
### Changed
- Updated dependency `toolarium-security` from `1.1.6` to `1.1.7`.
- Adapted all `SecurityManagerProviderFactory.getSecurityManagerProvider` call sites to the new `char[]` password parameter (`HttpServerTestUtil`, `HttpDtoTest`, `HttpServerTest`, `SslCertificateInspectorTest`).

## [ 1.1.2 ] - 2026-09-20
### Security
- Fixed HTTP response header CRLF injection: strip `\r` and `\n` from all response header values before writing (`AbstractConnectionHandler`).
- Fixed WHOIS SSRF via referral chain: referral targets are now validated against an allowlist of 8 known WHOIS registry suffixes; non-matching referrals are logged and skipped (`WhoisImpl`).
- Fixed JNDI URL injection in `DigImpl`: `dnsServer` parameter is now rejected if it contains `://`, `/`, or spaces before being placed into `java.naming.provider.url`.
- Fixed unbounded WHOIS response accumulation: responses are now capped at 512 KB; exceeding servers receive an `IOException` (`WhoisImpl`).
- Fixed log injection: raw HTTP request first line is now sanitized (CR/LF stripped) before being written to the DEBUG log (`HttpConnectionHandlerImpl`).

### Changed
- Fixed `HttpClient` created per request in `HttpClientUtilImpl`: `HttpClient` is now a shared field initialized once in the constructor, enabling connection pool reuse.
- Fixed per-call thread pool creation in `NsLookupImpl`: both single lookups (`resolveWithTimeout`) and batch lookups (`executeBatch`) now share a single class-level daemon thread pool of 20 threads instead of spawning a new executor on every call.
- Fixed `DigImpl.digAll`: all 6 record type queries now share a single JNDI `DirContext` instead of creating and tearing down a DNS context per query.
- Fixed URL-decode hot loop in `HttpRequestParser`: replaced `String.replaceAll()` (regex) with `String.replace()` (literal) for all percent-encoding pairs; corrected `%24` (`$`) and `%3F` (`?`) values that were incorrectly regex-escaped.
- Documented JVM limitation in `TracerouteImpl`: `InetAddress.isReachable(ttl)` silently falls back to TCP port 7 without ICMP/root privileges, making intermediate hop discovery non-functional on most systems.
- Fixed partial read bug in `AbstractConnectionHandler.readInBody`: `reader.read(char[])` return value is now checked in a loop to handle network fragmentation; body is no longer silently truncated.
- Fixed HTTP header flood DoS: `HttpHeaderUtil.readHeaders` now enforces a limit of 100 headers and 8 KB total header bytes, throwing `IOException` when exceeded.
- Fixed `NsLookupImpl.reverseLookup` having no timeout: now routes through `resolveWithTimeout` (same executor + `future.get(timeout)` as forward lookup).
- Fixed unclean server shutdown: `HttpServerImpl.stop()` now calls `awaitTermination(30s)` on the worker pool and `awaitTermination(5s)` on the accept loop before returning; falls back to `shutdownNow()` on timeout or `InterruptedException`.
- Fixed silent exception swallow in `PortScannerImpl.prepareResultSet`: caught exceptions are now logged at DEBUG level with stack trace.
- Fixed implicit referral depth limit in `WhoisImpl`: introduced explicit `MAX_REFERRALS = 1` constant with a guarded `while` loop and counter.
- Migrated `PortScannerImpl` from `java.util.logging` to SLF4J; all `LOG.debug` calls are now guarded by `isDebugEnabled()`.
- Updated dependency `toolarium-common` from `1.0.0` to `1.1.0`.

## [ 1.1.1 ] - 2026-08-02
### Fixed
- Fixed TLS hostname verification in `SslCertificateInspectorImpl`: replaced no-arg `SSLSocketFactory.createSocket()` with a plain socket connected first, then wrapped via `createSocket(plain, host, port, true)`. The no-arg overload produced an SSL socket with no peer hostname, so the JDK had no name to bind; the 4-arg overload preserves the hostname for correct peer identification if endpoint verification is ever enabled.

## [ 1.1.0 ] - 2026-05-14
### Added
- Added TCP ping feature (`PingFactory`, `IPing`, `TcpPingImpl`) for measuring network reachability and latency using NIO non-blocking channels.
- Supports single host, multi-host parallel ping, configurable timeout, and IPv6 bracket notation.
- Added DNS lookup feature (`NsLookupFactory`, `INsLookup`, `NsLookupImpl`) for forward and reverse DNS resolution with configurable timeout and parallel multi-host lookups.
- Added DNS dig feature (`DigFactory`, `IDig`, `DigImpl`) for querying specific DNS record types (A, AAAA, MX, CNAME, TXT, NS, SOA, PTR, SRV) with optional custom DNS server via JNDI.
- Added traceroute feature (`TracerouteFactory`, `ITraceroute`, `TracerouteImpl`) for tracing the network path to a host hop-by-hop.
- Added WHOIS lookup feature (`WhoisFactory`, `IWhois`, `WhoisImpl`) for querying domain/IP registration info with referral following.
- Added Wake-on-LAN feature (`WakeOnLanFactory`, `IWakeOnLan`, `WakeOnLanImpl`) for sending magic packets via UDP broadcast.
- Added network interface info utility (`NetworkInterfaceUtil`) for enumerating local NICs with IPs, MACs, MTU, and status.
- Added SSL certificate inspector (`SslCertificateInspectorFactory`, `ISslCertificateInspector`, `SslCertificateInspectorImpl`) for inspecting remote TLS certificates.
- Added subnet calculator (`SubnetCalculator`) for computing network/broadcast/first usable/last usable/host count from CIDR expressions.
- Added HTTP client utility (`HttpClientFactory`, `IHttpClientUtil`, `HttpClientUtilImpl`) with GET/POST/PUT/DELETE support and configurable timeout.
- Added proxy detector (`ProxyDetector`) for detecting system HTTP/HTTPS/SOCKS proxy settings.

### Changed
- Made HTTP server socket read timeout configurable via `setSocketTimeout(int)` (default 30s).
- Made HTTP server worker thread pool size configurable via `setWorkerPoolSize(int)` (default 100).
- Made maximum request body size configurable via `setMaxBodySize(int)` (default 10 MB).
- Replaced thread-unsafe `SimpleDateFormat` with thread-safe `DateTimeFormatter` for RFC 1123 date formatting.
- Replaced unbounded thread pool with bounded `ThreadPoolExecutor` and `CallerRunsPolicy` for backpressure.
- Executors are now created per `start()` call, allowing stop/start cycles without `RejectedExecutionException`.
- HTTP headers are now always read via blocking I/O instead of unreliable `reader.ready()` check.

### Fixed
- Fixed socket leak: client sockets are now always closed in the `finally` block.
- Fixed potential OOM via crafted `Content-Length` header or unbounded body read.
- Fixed `IOException` incorrectly delegated to `UncaughtExceptionHandler` instead of being handled gracefully.
- Fixed malformed `Content-Length` headers causing `NumberFormatException` instead of proper error response.
- Fixed RFC 1123 date pattern using 5-digit year (`yyyyy` instead of `yyyy`).

## [ 1.0.4 ] - 2025-01-03
### Changed
- http-server refactoring and added enhanced logging.
- Added HttpServerTestUtil for writing simpler test cases.
- Enhanced http-server tests.

### Fixed
- Bugfix in EchoService proper handling of GET requests.

## [ 1.0.3 ] - 2025-01-01
### Changed
- Updated library dependencies.
- Changed to java-library.

## [ 1.0.2 ] - 2024-08-03
### Changed
- Updated library dependencies.

## [ 1.0.1 ] - 2024-06-28
### Changed
- Updated library dependencies.

## [ 1.0.0 ] - 2023-12-11
### Added
- Small http server framework.
- Implemented services: echo, ping.

### Fixed
- Small bug fixes.

## [ 0.6.3 ] - 2023-06-22
### Fixed
- Small bug fixes.

## [ 0.6.2 ] - 2023-06-20
### Changed
- Setup initial version.
