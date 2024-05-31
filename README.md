![Last commit build](https://github.com/LasOri/komp/actions/workflows/on_commit.yml/badge.svg)

<img src="assets/KOMP.png" alt="KOMP" width="100" height="100">

# KOMP (Kotlin Model Provider)

KOMP is a Kotlin library designed to generate instances of `@Serializable` classes. It allows for easy setup of data sources for primitive and string types, as well as custom value generators for complex types such as enums.

## No promises

> `Disclaimer`
>
> This project is a proof of concept and heavily built on the inner behavior of the [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) library; therefore, the correct behavior of this library is not guaranteed.

But even with its limitations, it can be useful for testing purposes.

## Features

- Generate instances of `@Serializable` classes.
- Customize data sources for primitive and string types.
- Set up custom value generators for complex types like enums.
- Use annotations or direct method calls to generate data.
- Predefine values for specific properties when using direct method calls.

## Getting Started

### Installation

To use KOMP in your project, add the following dependency to your `build.gradle.kts` file:

```kotlin
dependencies {
    implementation("com.example:komp:1.0.0")
}
```

### Setup

You can set up KOMP by specifying data sources and custom value generators. Here's an example setup:
```kotlin
val testEnumGenerator = object : Generator<Convertible<*, *>> {
    override fun generate(): Convertible<*, *> {
        val testEnum = TestEnum.entries.toList().random()
        return Convertible(testEnum, TestEnum.serializer())
    }
}

Komp.setup(
    host = this,
    intType = IntType.prime,
    doubleType = DoubleType.famousConstants,
    stringType = StringType.movieQuote,
    testEnumGenerator
)
```
In this setup:
`intType` is set to generate prime numbers.
`doubleType` is set to generate famous constants.
`stringType` is set to generate movie quotes.
`testEnumGenerator` is a custom generator for TestEnum values.

### Usage

#### Annotation
You can use the `@Kompify` annotation to automatically generate data for a variable:
```kotlin
@Kompify
lateinit var testData: TestData
```

#### Direct Method Call
Alternatively, you can use a direct method call to generate data, with the option to set predefined values for specific properties:
```kotlin
val testData: TestData = komp.kompify(predefinedValues = mapOf(TestData::text to Convertible(expectedValue, String.serializer())))
```
In this example, the text property of TestData is predefined with the value expectedValue.

### Example
Here's a simple example demonstrating how to use KOMP:
```kotlin
// Define a serializable class
@Serializable
data class TestData(val text: String, val number: Int)

// Setup KOMP
Komp.setup(
    host = this,
    intType = IntType.prime,
    stringType = StringType.movieQuote
)

// Generate data using annotation
@Kompify
lateinit var testData: TestData

// Generate data using direct method call
val testData: TestData = komp.kompify(predefinedValues = mapOf(TestData::text to Convertible("Hello, World!", String.serializer())))
```

## License
This project is licensed under the MIT License - see the LICENSE file for details.