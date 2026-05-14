# toolarium-network

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [ 1.1.1 ] - 2026-05-14

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
