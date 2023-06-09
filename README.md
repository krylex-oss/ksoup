# Ksoup

![GitHub](https://img.shields.io/github/license/krylex-oss/ksoup)
[![Jitpack](https://jitpack.io/v/krylex-oss/ksoup.svg)](https://jitpack.io/#krylex-oss/ksoup)
![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Frepository.krylex.xyz%2Freleases%2Fxyz%2Fkrylex%2Fksoup%2Fmaven-metadata.xml)

## Install

### Add a new repository:

<p>
<details>

<summary>Jitpack</summary>

```kotlin
repositories { 
    maven("https://jitpack.io")
}
```

</details>
</p>
<p>
<details>

<summary>Krylex</summary>

```kotlin
repositories {
    maven("https://repository.krylex.xyz/releases")
}
```

</details>
</p>

### Add dependency:

```kotlin
dependencies {
    implementation("xyz.krylex:ksoup:1.1.0")
}
```

## Usage

### Plugin installation into ktor
```kotlin
HttpClient(CIO) {
    install(Ksoup)
}
```
or
```kotlin
HttpClient(CIO) {
    ksoup()
}
```

### Document parsing
If the content type of the response does not match any of the registered parsers, it **throws** a ```BadContentTypeException```:
```kotlin
val document: Document = client.get(url).body()
```

### Document parsing (nullable variant)
If the content type of the response does not match any of the registered parsers, it **returns null**:
```kotlin
val document: Document? = client.get(url).body()
```

### Document parsing (WITHOUT plugin)
You can use extension methods without installing Ksoup plugin on Ktor http client:
```kotlin
val document = client.document(url)
val documentNullable = client.documentOrNull(url)
```

You can use additional parsers for content types:
```kotlin
val customParsers = mapOf(ContentType.Text.Plain to Parser.htmlParser())

val document = client.document(url, customParsers)
val documentNullable = client.documentOrNull(url, customParsers)
```

Also, you can work with **HttpResponse**:
```kotlin
val response: HttResponse = client.get(url)

val document = response.document()
val documentNullable = response.documentOrNull()
```

<!-- More information in documentation: url -->

## TODO

- [ ] Documentation generation with Dokka
- [ ] Documentation deploying to GitHub Pages