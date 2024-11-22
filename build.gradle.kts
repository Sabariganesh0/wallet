plugins {
	id("java")
	id("org.springframework.boot") version "3.3.4"
	id("io.spring.dependency-management") version "1.1.6"
}

group = "com.mainproject"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-mail")
	implementation("jakarta.validation:jakarta.validation-api:3.1.0")
	implementation("io.jsonwebtoken:jjwt:0.9.1") // For JWT token implementation
	implementation("javax.xml.bind:jaxb-api:2.3.1")
	implementation("org.glassfish.jaxb:jaxb-runtime:2.3.1")
	implementation("jakarta.mail:jakarta.mail-api:2.0.1")
	implementation("com.sun.mail:jakarta.mail:2.0.1")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	compileOnly("org.projectlombok:lombok:1.18.30") // Use the latest version available
	annotationProcessor("org.projectlombok:lombok:1.18.30") // For annotation processing
	implementation("org.springframework.data:spring-data-mongodb")
	// Testing dependencies
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.mockito:mockito-core:5.4.0")
	testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")


	implementation("jakarta.validation:jakarta.validation-api:3.0.0")
	implementation("org.hibernate.validator:hibernate-validator:6.2.0.Final") // or latest version
	implementation("org.glassfish:javax.el:3.0.0")
	implementation("org.springframework:spring-tx")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
