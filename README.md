[![License](https://img.shields.io/github/license/toolarium/toolarium-network)](https://github.com/toolarium/toolarium-network/blob/master/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.toolarium/toolarium-network/1.1.0)](https://search.maven.org/artifact/com.github.toolarium/toolarium-network/1.1.0/jar)
[![javadoc](https://javadoc.io/badge2/com.github.toolarium/toolarium-network/javadoc.svg)](https://javadoc.io/doc/com.github.toolarium/toolarium-network)

# toolarium-network

Java library with network utilities providing:

- **IP Utilities** — IPv4/IPv6 address validation and parsing (`IPUtil`), CIDR range checking and subnet enumeration (`CIDRUtil`), IPv6 address formatting (`IPV6Formatter`), subnet calculator (`SubnetCalculator`).
- **TCP Ping** — NIO-based non-blocking TCP ping for measuring reachability and latency to single or multiple hosts in parallel (`PingFactory`).
- **DNS Lookup** — Forward and reverse DNS lookups with configurable timeout and parallel multi-host resolution (`NsLookupFactory`).
- **DNS Dig** — Query specific DNS record types (A, AAAA, MX, CNAME, TXT, NS, SOA, PTR, SRV) with optional custom DNS server (`DigFactory`).
- **Traceroute** — Trace the network path to a host hop-by-hop with latency per hop (`TracerouteFactory`).
- **Whois** — Query domain/IP registration info via WHOIS protocol with referral following (`WhoisFactory`).
- **Wake-on-LAN** — Send magic packets to wake remote machines by MAC address (`WakeOnLanFactory`).
- **Network Interface Info** — Enumerate local NICs with IPs, MACs, MTU, and status (`NetworkInterfaceUtil`).
- **SSL Certificate Inspector** — Inspect remote TLS certificates (expiry, issuer, SANs, chain, protocol) (`SslCertificateInspectorFactory`).
- **Port Scanner** — Configurable multi-threaded TCP port scanner with listener support (`PortScannerFactory`).
- **HTTP Client** — Simple GET/POST/PUT/DELETE helpers with response parsing and configurable timeout (`HttpClientFactory`).
- **Proxy Detector** — Detect system proxy settings for HTTP/HTTPS/SOCKS (`ProxyDetector`).
- **HTTP Server** — Lightweight embedded HTTP/HTTPS server framework with pluggable services (`HttpServerFactory`). Includes built-in `PingService` and `EchoService`.

## Built With

* [cb](https://github.com/toolarium/common-build) - The toolarium common build

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/toolarium/toolarium-network/tags). 


### Gradle:

```groovy
dependencies {
    implementation "com.github.toolarium:toolarium-network:1.1.0"
}
```

### Maven:

```xml
<dependency>
    <groupId>com.github.toolarium</groupId>
    <artifactId>toolarium-network</artifactId>
    <version>1.1.0</version>
</dependency>
```


### HTTP Server Configuration

The HTTP server supports the following configuration (call before `start()`):

| Method | Default | Description |
|--------|---------|-------------|
| `setSocketTimeout(int)` | 30,000 ms | Read timeout per connection. Protects against slow/idle clients. |
| `setWorkerPoolSize(int)` | 100 | Number of worker threads for handling requests. |
| `setMaxBodySize(int)` | 10 MB | Maximum allowed request body size. |

```java
IHttpServer server = HttpServerFactory.getInstance().getServerInstance();
((HttpServerImpl) server).setSocketTimeout(60_000);
((HttpServerImpl) server).setWorkerPoolSize(50);
server.start(new EchoService(), 8080);
```

### TCP Ping Usage

```java
// Ping a single host
IPingResult result = PingFactory.getInstance().ping("google.com", 80);
// result.isReachable(), result.getDuration()

// Ping multiple hosts in parallel
List<IPingResult> results = PingFactory.getInstance().ping(80, "google.com", "github.com");

// Custom timeout (5 seconds)
IPing ping = PingFactory.getInstance().getPing(5000);
List<IPingResult> results = ping.pingTargets(80, "host1:8080", "host2:443", "[::1]:9090");
```

### DNS Lookup Usage

```java
// Forward lookup — resolve hostname to IP addresses
INsLookupResult result = NsLookupFactory.getInstance().lookup("github.com");
// result.getHostname(), result.getAddresses(), result.getDuration()

// Reverse lookup — resolve IP to hostname
INsLookupResult reverse = NsLookupFactory.getInstance().reverseLookup("127.0.0.1");

// Multi-host parallel lookup
List<INsLookupResult> results = NsLookupFactory.getInstance().lookup("google.com", "github.com");

// Custom timeout (3 seconds)
INsLookup nsLookup = NsLookupFactory.getInstance().getNsLookup(3000);
INsLookupResult result = nsLookup.lookup("example.com");
```

### DNS Dig Usage

```java
// Query A records
IDigResult result = DigFactory.getInstance().dig("github.com", DnsRecordType.A);
// result.getRecords() — list of DnsRecord with type, value, priority

// Query MX records
IDigResult mx = DigFactory.getInstance().dig("github.com", DnsRecordType.MX);

// Query all common record types (A, AAAA, MX, CNAME, TXT, NS)
List<IDigResult> all = DigFactory.getInstance().digAll("github.com");

// Custom DNS server and timeout
IDig dig = DigFactory.getInstance().getDig(3000, "8.8.8.8");
IDigResult result = dig.dig("example.com", DnsRecordType.TXT);
```

### Traceroute Usage

```java
ITracerouteResult result = TracerouteFactory.getInstance().trace("github.com");
for (ITracerouteHop hop : result.getHops()) {
    System.out.println(hop); // "1  gateway (10.0.0.1)  2ms"
}
```

### Whois Usage

```java
IWhoisResult result = WhoisFactory.getInstance().query("example.com");
// result.getFields() — parsed key-value pairs (Domain Name, Registrar, etc.)
// result.getRawResponse() — full WHOIS text
```

### Wake-on-LAN Usage

```java
IWakeOnLanResult result = WakeOnLanFactory.getInstance().wake("AA:BB:CC:DD:EE:FF");
// Sends magic packet via UDP broadcast
// Custom broadcast: wake("AA:BB:CC:DD:EE:FF", "192.168.1.255")
```

### Network Interface Info Usage

```java
List<INetworkInterfaceInfo> interfaces = NetworkInterfaceUtil.getInstance().getNetworkInterfaces();
List<INetworkInterfaceInfo> active = NetworkInterfaceUtil.getInstance().getActiveNetworkInterfaces();
INetworkInterfaceInfo loopback = NetworkInterfaceUtil.getInstance().getLoopbackInterface();
```

### SSL Certificate Inspector Usage

```java
ISslCertificateInfo cert = SslCertificateInspectorFactory.getInstance().inspect("github.com");
// cert.getSubjectDN(), cert.getIssuerDN(), cert.getNotAfter()
// cert.isValid(), cert.getDaysUntilExpiry(), cert.getProtocol()
```

### Subnet Calculator Usage

```java
ISubnetInfo info = SubnetCalculator.getInstance().calculate("192.168.1.0/24");
// info.getNetworkAddress() -> "192.168.1.0"
// info.getBroadcastAddress() -> "192.168.1.255"
// info.getFirstUsableAddress() -> "192.168.1.1"
// info.getLastUsableAddress() -> "192.168.1.254"
// info.getSubnetMask() -> "255.255.255.0"
// info.getUsableHostCount() -> 254
```

### HTTP Client Usage

```java
// Simple GET
IHttpClientResult result = HttpClientFactory.getInstance().get("http://example.com/api");
// result.getStatusCode(), result.getBody(), result.getHeaders()

// POST with body
IHttpClientResult post = HttpClientFactory.getInstance().post("http://example.com/api", "{\"key\":\"value\"}", "application/json");

// Custom timeout
IHttpClientUtil client = HttpClientFactory.getInstance().getHttpClient(5000);
IHttpClientResult result = client.put("http://example.com/api/1", "data", "text/plain");
client.delete("http://example.com/api/1");
```

### Proxy Detection Usage

```java
List<IProxyInfo> proxies = ProxyDetector.getInstance().detectHttpProxies();
boolean hasProxy = ProxyDetector.getInstance().hasProxy("http://example.com");
List<IProxyInfo> custom = ProxyDetector.getInstance().detectProxies("https://internal.company.com");
```

### Port Scanner Usage

```java
// Scan open ports on localhost, range 1-1024, with 20 threads and 200ms timeout
Map<String, List<Integer>> openPorts =
    PortScannerFactory.getInstance().scanOpenPorts("127.0.0.1", 1, 1024, 20, 200);
```


