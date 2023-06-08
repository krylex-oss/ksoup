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
    implementation("xyz.krylex:ksoup:1.0.0")
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
Warning! If the content type of the response does not match any of the registered parsers, it throws a ```BadContentTypeException```.
```kotlin
val document: Document = client.get(url).body()
```
or via extension
```kotlin
val document = client.getDocument(url)
```

### Document parsing (nullable variant)
If the content type of the response does not match any of the registered parsers, it returns null.
```kotlin
val document: Document? = client.getDocumentOrNull(url)
```

## TODO

- [ ] Documentation generation with Dokka
- [ ] Documentation deploying to GitHub Pages