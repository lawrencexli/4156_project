plugins {
	java
	jacoco
	id("org.springframework.boot") version "3.1.4"
	id("io.spring.dependency-management") version "1.1.3"
	id("com.github.spotbugs") version "4.4.2"
	id("checkstyle")
}

group = "com.project"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

checkstyle {
	toolVersion = "8.45.1"
	configFile = file("config/checkstyle/sun_checks.xml")
	isIgnoreFailures = false
}

jacoco {
	toolVersion = "0.8.9"
	reportsDirectory = layout.buildDirectory.dir("reports/jacocoXml")
}

extra["springCloudGcpVersion"] = "4.8.0"
extra["springCloudVersion"] = "2022.0.4"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.google.cloud:spring-cloud-gcp-starter-storage")
	implementation("com.github.spotbugs:spotbugs-annotations:4.0.1")
	implementation("org.mockito:mockito-core:5.6.0")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	spotbugs("com.github.spotbugs:spotbugs:4.4.2")
	spotbugsPlugins("com.h3xstream.findsecbugs:findsecbugs-plugin:1.11.0")
}

dependencyManagement {
	imports {
		mavenBom("com.google.cloud:spring-cloud-gcp-dependencies:${property("springCloudGcpVersion")}")
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

tasks.withType<Checkstyle>().configureEach {
	reports {
		xml.required = true
		html.required = true
	}
}

tasks.spotbugsMain {
	reports.create("html") {
		required.set(true)
		outputLocation.set(file("$buildDir/reports/spotbugsHtml/spotbugsMain.html"))
		setStylesheet("fancy-hist.xsl")
	}
}

tasks.spotbugsTest {
	reports.create("html") {
		required.set(true)
		outputLocation.set(file("$buildDir/reports/spotbugsHtml/spotbugsTest.html"))
		setStylesheet("fancy-hist.xsl")
	}
}

tasks.test {
	finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
	reports {
		junitXml.outputLocation = layout.buildDirectory.dir("reports/junit5")
	}
}

tasks.jacocoTestReport {
	dependsOn(tasks.test) // tests are required to run before generating the report
	reports {
		xml.required = false
		csv.required = false
		html.outputLocation = layout.buildDirectory.dir("reports/jacocoHtml")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}


